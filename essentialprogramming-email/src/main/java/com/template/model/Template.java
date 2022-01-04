package com.template.model;

import java.util.Optional;

public enum Template {

    PARENT_HTML("html/parent"),

    NEW_USER("html/new_user", "new_user", PARENT_HTML),
    NEW_ADMIN("html/new_admin", "new_user", PARENT_HTML),

    PROFILE_PDF("html/profilepdf"),

    SKILL_SUGGESTION_ACCEPTED("html/skill_suggestion_accepted"),
    SKILL_SUGGESTION_DECLINED("html/skill_suggestion_declined");

    public String page;
    public String fragment = null;
    public Template master = null;

    Template(String page) {
        this.page = page;
    }

    Template(String page, String fragment, Template master) {
        this.page = page;
        this.fragment = fragment;
        this.master = master;
    }

    public Optional<String> getPage() {
        return Optional.of(page);
    }

    public Optional<String> getFragment() {
        return Optional.ofNullable(fragment);
    }

    public Optional<Template> getMaster() {
        return Optional.ofNullable(master);
    }
}
