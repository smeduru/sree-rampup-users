package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.RoleDao;
import com.sree.rampup.users.dao.UserDao;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;
import com.sree.rampup.users.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * User Service Implementation
 */
@Service
@Transactional
public class UserService extends BaseService<User> {
    @Autowired
    private RoleDao roleDao;

    public UserService(UserDao userDao, RoleDao roleDao) {
        super(userDao);
        this.roleDao = roleDao;
    }

    /**
     * Check for duplicate users using business key
     * @param user
     * @return
     */
    @Override
    protected boolean checkForDuplicates(User user) {
        User user1 = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail()).build();

        Example<User> example = Example.of(user1);
        Optional<User> userOptional = dao.findOne(example);
        return userOptional.isPresent();
    }

    /**
     * Assign a role to the given user
     * @param userId
     * @param roleId
     */
    public void assignRole(UUID userId, UUID roleId) {
        updateRole(userId, roleId, false);
    }

    public void removeRole(UUID userId, UUID roleId) {
        updateRole(userId, roleId, true);
    }

    /**
     * Helper method for updating the role
     */
    private void updateRole(UUID userId, UUID roleId, boolean isDelete) {
        Role role = roleDao.getOne(roleId);
        User user = dao.getOne(userId);
        if (role == null || user == null) {
            throw new EntityNotFoundException();
        }
        if (isDelete) {
            user.removeRole(role);
        }
        else {
            user.addRole(role);
        }
        dao.save(user);
    }

    /**
     * Finds all roles on a given user
     * @param userId
     * @return
     */
    public Set<Role> findAllRoles(UUID userId) {
        User user = get(userId);
        return user.getRoles();
    }

    /**
     * Check for a given role on the user provided
     * @param userId
     * @param roleId
     * @return
     */
    public Role findRole(UUID userId, UUID roleId) {
        User user = get(userId);
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new EntityNotFoundException();
        }
        Optional<Role> optional = user.getRoles().stream().filter(r -> roleId.equals(r.getId())).findFirst();
        return (optional.isPresent()) ? optional.get() : null;
    }
}
