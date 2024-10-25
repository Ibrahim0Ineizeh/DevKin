package com.example.devkin.mappings;

import com.example.devkin.dtos.ProjectDto;
import com.example.devkin.entities.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toProjectDto(Project project);
}
