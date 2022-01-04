package com.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ProfileSkillSearchCriteria {

    @Size(min = 2, message = "Name search criteria must have at least 2 characters!")
    private String name;

    @JsonIgnore
    public boolean isEmpty(){
        return name == null;
    }
}
