package com.sree.rampup.users.controller;

import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;
import com.sree.rampup.users.service.UserService;
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
 * Rest Controller for maintaining users
 */
@RestController
@RequestMapping(value = "/v1/user")
@Slf4j
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Adds a given user
     */
    @PostMapping
    public User addUser(@Valid @RequestBody final User user) {
        User result = userService.add(user);
        logger.debug("Added User[%s]", user);
        return result;
    }

    /**
     * Find all users
     * @return all users
     */
    @GetMapping("/")
    public List<User> findAll() {
        List<User> users = userService.findAll();
        return users;
    }

    /**
     * Finds and returns user for a given user id
     * @param uuid
     * @return
     */
    @GetMapping("/{uuid}")
    public User getUser(@PathVariable UUID uuid) {
        User user = userService.get(uuid);
        return user;
    }

    /**
     * Updates the given user
     * @param uuid
     * @param user
     */
    @PutMapping("/{uuid}")
    public void updateUser(@PathVariable UUID uuid, @Valid @RequestBody final User user) {
        user.setId(uuid);
        userService.update(user);
    }

    /**
     * Delete the user using the given user id
     * @param uuid
     */
    @DeleteMapping("/{uuid}")
    public void deleteUser(@PathVariable UUID uuid) {
        userService.delete(uuid);
    }

    /**
     * Finds all roles on a given user id
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/role/")
    public Set<Role> findAllRoles(@NotNull @PathVariable UUID userId) {
        Set<Role> roles = userService.findAllRoles(userId);
        return roles;
    }

    /**
     * Find a specific role based on a given user id and role id
     * @param userId
     * @param roleId
     * @return
     */
    @GetMapping("/{userId}/role/{roleId}")
    public Role findRole(@NotNull @PathVariable UUID userId, @PathVariable UUID roleId) {
        Role role = userService.findRole(userId, roleId);
        return role;
    }

    /**
     * Assigns the role to the user using given user id and role id
     * @param userId
     * @param roleId
     */
    @PutMapping("/{userId}/role/{roleId}")
    public void assignRoleToUser(@NotNull @PathVariable UUID userId, @NotNull @PathVariable UUID roleId) {
        userService.assignRole(userId, roleId);
    }

    /**
     * Removes a given role on the provided user id
     * @param userId
     * @param roleId
     */
    @DeleteMapping("/{userId}/role/{roleId}")
    public void removeRoleFromUser(@NotNull @PathVariable UUID userId, @NotNull @PathVariable UUID roleId) {
        userService.removeRole(userId, roleId);
    }
}
