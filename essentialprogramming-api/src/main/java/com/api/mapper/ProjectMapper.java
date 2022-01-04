package com.api.mapper;

import com.api.entities.Project;
import com.api.model.ProjectInput;
import com.api.output.ProjectJSON;

public class ProjectMapper {

    public static Project inputToEntity(ProjectInput projectInput) {
        return Project.builder()
                .title(projectInput.getTitle())
                .shortDescription(projectInput.getShortDescription())
                .period(projectInput.getPeriod())
                .responsibilities(projectInput.getResponsibilities())
                .build();
    }

    public static ProjectJSON entityToJSON(Project project) {
        return ProjectJSON.builder()
                .title(project.getTitle())
                .shortDescription(project.getShortDescription())
                .period(project.getPeriod())
                .responsibilities(project.getResponsibilities())
                .build();
    }

    public static Project inputToProject(ProjectInput projectInput) {
        return Project.builder()
                .title(projectInput.getTitle())
                .shortDescription(projectInput.getShortDescription())
                .period(projectInput.getPeriod())
                .responsibilities(projectInput.getResponsibilities())
                .build();

    }
}
