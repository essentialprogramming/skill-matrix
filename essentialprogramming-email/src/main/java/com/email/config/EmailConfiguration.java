package com.email.config;

import com.crypto.Crypt;
import com.email.service.EmailService;
import com.email.service.SendGridEmailService;
import com.util.cloud.ConfigurationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;

@Configuration
public class EmailConfiguration {

    @Bean
    public EmailService loadEmailService() {
        return new SendGridEmailService();
    }
}
