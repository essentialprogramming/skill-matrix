package com.api.repository.impl;

import com.api.entities.ProfileSkill;
import com.api.model.ProfileSkillSearchCriteria;
import com.api.repository.ProfileSkillRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileSkillRepositoryCustomImpl implements ProfileSkillRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProfileSkill> searchProfileSkills(ProfileSkillSearchCriteria profileSkillSearchCriteria, String userEmail) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ProfileSkill> criteriaQuery = criteriaBuilder.createQuery(ProfileSkill.class);
        final Root<ProfileSkill> profileSkillRoot = criteriaQuery.from(ProfileSkill.class);

        criteriaQuery.select(profileSkillRoot);

        List<Predicate> predicates = getFilterPredicates(profileSkillSearchCriteria, criteriaBuilder,profileSkillRoot, userEmail);
        if(!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private List<Predicate> getFilterPredicates(ProfileSkillSearchCriteria profileSkillSearchCriteria,
                                                CriteriaBuilder builder, Root<ProfileSkill> profileSkillRoot, String userEmail) {

        Predicate nameCondition = builder.like(profileSkillRoot.get("skill").get("name"), "%" + profileSkillSearchCriteria.getName() + "%");
        Predicate profileCondition = builder.like(profileSkillRoot.get("profile").get("email"), "%" + userEmail + "%");

        return Stream.of(nameCondition, profileCondition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
