package com.sree.rampup.users.util;


import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;

import java.util.*;

import static java.util.UUID.randomUUID;

/**
 * Builder for creating mock Role models.
 */
public class RoleBuilder {
    public static Role mockRoleForAdd() {
        final String NAME = "APP_USER_ROLE";

        return Role.builder()
                .name(NAME)
                .build();
    }

    public static Role getMockRole() {
        final String NAME = "SUPER_ADMIN_ROLE";

        return Role.builder()
                .name(NAME)
                .id(randomUUID())
                .build();
    }

    public static Role getMockRole(UUID userId) {
        Role role = getMockRole();
        User user = UserBuilder.getMockUser();
        Set<User> set = new HashSet<User>();
        set.add(user);
        user.setId(userId);
        role.setUsers(set);
        return role;
    }

    public static List<Role> mockRolesForFindAll() {
        List<Role> roles = new ArrayList<>();

        Role role = mockRoleForAdd();
        role.setId(randomUUID());
        roles.add(role);

        role = getMockRole();
        roles.add(role);

        return roles;
    }
}
