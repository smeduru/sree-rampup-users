package com.sree.rampup.users.util;


import com.sree.rampup.users.model.Permission;

import java.util.*;

/**
 * Builder for creating mock permission objects
 */
public class PermissionBuilder {

    public static Permission mockPermissionForAdd() {
        final String PERMISSION_NAME = "ADD_CONNECTOR";

        return Permission.builder()
                .name(PERMISSION_NAME)
                .isEnabled(true)
                .build();
    }

    public static Permission mockPermission() {
        Permission permission = mockPermissionForAdd();
        permission.setId(UUID.randomUUID());
        return permission;
    }

    public static Set<Permission>  mockPermissions() {
        Set<Permission> permissions = new HashSet<>();

        Permission permission = mockPermissionForAdd();
        permission.setId(UUID.randomUUID());
        permissions.add(permission);

        permission = mockPermission();
        permissions.add(permission);

        return permissions;

    }

    public static List<Permission> mockPermissionsForFindAll() {
        Set<Permission> permissionSet = mockPermissions();
        List<Permission> permissionList = new ArrayList<>();
        permissionList.addAll(permissionSet);
        return permissionList;
    }
}
