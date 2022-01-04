package com.api.service;

import com.api.entities.*;
import com.api.env.resources.AppResources;
import com.api.mapper.UserMapper;
import com.api.model.*;
import com.api.output.UserJSON;
import com.api.repository.*;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.crypto.PasswordHash;
import com.email.service.EmailManager;
import com.internationalization.EmailMessages;
import com.internationalization.Messages;
import com.template.model.Template;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.PlatformType;
import com.util.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final EmailManager emailManager;
    private final UserPlatformRepository userPlatformRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmailManager emailManager, UserPlatformRepository userPlatformRepository) {

        this.userRepository = userRepository;
        this.emailManager = emailManager;
        this.userPlatformRepository = userPlatformRepository;
    }

    @Transactional
    public UserJSON saveUser(UserInput input, com.util.enums.Language language, PlatformType platformType) throws GeneralSecurityException {

        final User user = UserMapper.inputToUser(input);
        final User result = save(user, input, language, platformType);

        String loginUrl = AppResources.APP_URL.value() + "/api/auth/token";

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("fullName", user.getFullName());
        templateVariables.put("userEmail", user.getEmail());
        templateVariables.put("loginLink", loginUrl);

        if (platformType.equals(PlatformType.EMPLOYEE)) {
            emailManager.send(user.getEmail(),
                    EmailMessages.get("new_user.subject", language.getLocale()), Template.NEW_USER, templateVariables, language.getLocale());
        } else
            emailManager.send(user.getEmail(),
                    EmailMessages.get("new_user.subject", language.getLocale()), Template.NEW_ADMIN, templateVariables, language.getLocale());

        return UserMapper.userToJson(result);
    }


    private User save(User user, UserInput input, com.util.enums.Language language, PlatformType platformType) throws GeneralSecurityException {

        String uuid = NanoIdUtils.randomNanoId();

        user.setUserKey(uuid);

        userRepository.save(user);
        if (user.getId() > 0) {
            logger.debug("Start password hashing");
            String password = PasswordHash.encode("P@rola123");
            logger.debug("Finished password hashing");

            user.setPassword(password);
        }

        userPlatformRepository.save(
                UserPlatform.builder()
                        .roles(Collections.singletonList(String.valueOf(platformType)))
                        .platformType(platformType)
                        .user(user)
                        .build()
        );

        return user;
    }

    @Transactional
    public boolean checkAvailabilityByEmail(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        return !user.isPresent();
    }


    @Transactional
    public UserJSON loadUser(String email, com.util.enums.Language language) throws ApiException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            logger.info("User loaded={}", email);
            return UserMapper.userToJson(user.get());
        } else
            throw new ApiException(Messages.get("USER.NOT.FOUND", language), HTTPCustomStatus.INVALID_REQUEST);

    }

    @Transactional
    public boolean isPlatformAvailable(PlatformType platform, String email) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            for (UserPlatform userPlatform : user.get().getUserPlatformList()
            ) {
                if (userPlatform.getPlatformType() == platform) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    public UserJSON addPlatform(PlatformType platform, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found!"));

        UserPlatform userPlatform = UserPlatform.builder()
                .platformType(platform)
                .roles(Collections.singletonList(String.valueOf(platform)))
                .user(user)
                .build();

        userPlatformRepository.save(userPlatform);
        return UserMapper.userToJson(user);
    }
}
