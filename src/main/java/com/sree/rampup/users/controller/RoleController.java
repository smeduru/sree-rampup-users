package com.sree.rampup.users.controller;

import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * RestController for maintaining roles
 */
@RestController
@RequestMapping(value = "/v1/role")
@Slf4j
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    /**
     * Adds a given role
     * @param role
     * @return
     */
    @PostMapping
    public Role addRole(@Valid @RequestBody final Role role) {
        Role result = roleService.add(role);
        logger.debug("Added Role[%s]", role);
        return result;
    }

    /**
     * Finds  all existing roles
     * @return
     */
    @GetMapping("/")
    public List<Role> findAll() {
        List<Role> roles = roleService.findAll();
        return roles;
    }

    /**
     * Find a specific role for the given role id
     * @param uuid
     * @return
     */
    @GetMapping("/{uuid}")
    public Role getRole(@PathVariable UUID uuid) {
        Role role = roleService.get(uuid);
        return role;
    }

    /**
     * Updates the role
     * @param uuid
     * @param role
     */
    @PutMapping("/{uuid}")
    public void updateRole(@NotNull @PathVariable UUID uuid, @Valid @RequestBody final Role role) {
        role.setId(uuid);
        roleService.update(role);
    }

    /**
     * Deletes a role
     * @param uuid
     */
    @DeleteMapping("/{uuid}")
    public void deleteRole(@NotNull @PathVariable UUID uuid) {
        roleService.delete(uuid);
    }


    //Permissions: Begin
    /**
     * Delete a permission on a given role id and permission id
     * @param roleId
     * @param permissionId
     */
    @DeleteMapping("/{roleId}/permission/{permissionId}")
    public void deletePermission(@NotNull @PathVariable UUID roleId, @NotNull  @PathVariable UUID permissionId) {
        roleService.deletePermission(roleId, permissionId);
    }

    /**
     * Assigns a permission to the role
     * @param roleId
     * @param permission
     */
    @PutMapping("/{roleId}/permission")
    public void assignPermission(@NotNull @PathVariable UUID roleId, @NotNull @RequestBody Permission permission) {
        roleService.assignPermission(roleId, permission);
    }

    /**
     * Find all permissions on a given role
     * @param roleId
     * @return
     */
    @GetMapping("/{roleId}/permission/")
    public Set<Permission> findAllPermissions(@NotNull @PathVariable UUID roleId) {
        return roleService.findAllPermissions(roleId);
    }

    /**
     * Removes all permissions on a given role
     * @param roleId
     */
    @DeleteMapping("/{roleId}/permission/")
    public void removeAllPermissions(@PathVariable UUID roleId) {
        roleService.removeAllPermissions(roleId);
    }
    //Permissions: End
}
