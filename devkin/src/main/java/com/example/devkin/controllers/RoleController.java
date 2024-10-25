package com.example.devkin.controllers;

import com.example.devkin.dtos.RoleDto;
import com.example.devkin.entities.ProjectDeveloperRole;
import com.example.devkin.repositories.ProjectDeveloperRoleRepository;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.repositories.UserRepository;
import com.example.devkin.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/project/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectDeveloperRoleRepository projectDeveloperRoleRepository;

    @PostMapping("/assign")
    public ResponseEntity<String> assignRole(@RequestBody RoleDto roleDto) {

        ProjectDeveloperRole assignedRole = roleService.assignRole(
                projectRepository.findBySlug(roleDto.getProjectSlug()).get().getProjectId(),
                userRepository.findByEmail(roleDto.getUserEmail()).get().getId(),
                roleDto.getRole());

        if (assignedRole != null) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRole(@RequestBody RoleDto roleDto) {

        ProjectDeveloperRole updatedRole = roleService.updateRole(
                projectRepository.findBySlug(roleDto.getProjectSlug()).get().getProjectId(),
                userRepository.findByEmail(roleDto.getUserEmail()).get().getId(),
                roleDto.getRole());

        if (updatedRole != null) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/role")
    public ResponseEntity<String> getDeveloperRole(
            @RequestBody RoleDto roleDto) {

        ProjectDeveloperRole projectDeveloperRole = projectDeveloperRoleRepository
                .findByProject_ProjectIdAndDeveloper_Id(
                        projectRepository.findBySlug(roleDto.getProjectSlug()).get().getProjectId(),
                        userRepository.findByEmail(roleDto.getUserEmail()).get().getId());

        if (projectDeveloperRole != null) {
            return ResponseEntity.ok(projectDeveloperRole.getRole());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeRole(@RequestBody RoleDto roleDto) {

        roleService.removeRole(
                projectRepository.findBySlug(roleDto.getProjectSlug()).get().getProjectId(),
                userRepository.findByEmail(roleDto.getUserEmail()).get().getId());

        return ResponseEntity.noContent().build();
    }
}
