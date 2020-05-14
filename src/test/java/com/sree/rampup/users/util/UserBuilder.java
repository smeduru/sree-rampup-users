package com.sree.rampup.users.util;


import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;

import java.util.*;

import static java.util.UUID.randomUUID;

/**
 * Builder for creating mock User models.
 */
public class UserBuilder {

    public static User mockUserForAdd() {
        final String FIRST_NAME = "Donnie";
        final String LAST_NAME = "Mattox";
        final String EMAIL = "donnie.mattox@test.com";

        return User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();
    }

    public static User getMockUser() {
        final String FIRST_NAME = "Allison";
        final String LAST_NAME = "Patel";
        final String EMAIL = "allison.patel@test.com";

        return User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .id(randomUUID())
                .build();
    }

    public static List<User> mockUsersForFindAll() {
        List<User> users = new ArrayList<>();

        User user = mockUserForAdd();
        user.setId(randomUUID());
        users.add(user);

        user = getMockUser();
        users.add(user);

        return users;
    }

    public static Set<Role> mockFindAllRolesForUserId() {
        Set<Role> roles = new HashSet<>();

        roles.add(Role.builder().id(randomUUID()).name("ADMIN_ROLE").build());
        roles.add(Role.builder().id(randomUUID()).name("APP_USER_ROLE").build());

        return roles;
    }
}
