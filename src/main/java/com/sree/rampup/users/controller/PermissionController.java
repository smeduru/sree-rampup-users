package com.sree.rampup.users.controller;

import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RestController for maintaining permissions
 */
@RestController
@RequestMapping(value = "/v1/permission")
@Slf4j
public class PermissionController {
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    /**
     * Find all existing permissions
     * @return
     */
    @GetMapping("/")
    public List<Permission> findAll() {
        List<Permission> permissions = permissionService.findAll();
        return permissions;
    }

    /**
     * Returns Permission details on a given permission id
     * @param uuid Permission Id
     * @return
     */
    @GetMapping("/{uuid}")
    public Permission getPermission(@PathVariable UUID uuid) {
        Permission permission = permissionService.get(uuid);
        return permission;
    }

    /**
     * Enables/Disables a permission.
     * @param uuid Permission Id
     * @param map Consists of enabled -> [true|false]
     */
    @PutMapping("/{uuid}")
    public void enablePermission(@PathVariable UUID uuid, @NotNull @RequestBody final Map<String, Boolean> map) {
        boolean isEnabled = map.get("enabled");
        permissionService.enablePermission(uuid, isEnabled);
    }
}
