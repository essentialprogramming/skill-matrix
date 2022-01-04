package com.api.repository.impl;

import com.api.entities.CategorySkillRelation;
import com.api.model.SkillSearchCriteria;
import com.api.repository.SkillRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkillRepositoryCustomImpl implements SkillRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CategorySkillRelation> searchSkills(SkillSearchCriteria skillSearchCriteria) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<CategorySkillRelation> criteriaQuery = criteriaBuilder.createQuery(CategorySkillRelation.class);
        final Root<CategorySkillRelation> categorySkillRelationRoot = criteriaQuery.from(CategorySkillRelation.class);

        criteriaQuery.select(categorySkillRelationRoot);

        List<Predicate> predicates = getFilterPredicates(skillSearchCriteria, criteriaBuilder, categorySkillRelationRoot);
        if(!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    private List<Predicate> getFilterPredicates(SkillSearchCriteria skillSearchCriteria, CriteriaBuilder builder, Root<CategorySkillRelation> categorySkillRelationRoot) {

        Predicate nameCondition = builder.like(categorySkillRelationRoot.get("skill").get("name"), "%" + skillSearchCriteria.getName() + "%");
        Predicate categoryCondition = builder.like(categorySkillRelationRoot.get("category").get("categoryName"), "%" + skillSearchCriteria.getCategory() + "%");
        Predicate skillCondition = builder.or(nameCondition, categoryCondition);

        return Stream.of(skillCondition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
