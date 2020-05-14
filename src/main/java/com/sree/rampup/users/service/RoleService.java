package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.PermissionDao;
import com.sree.rampup.users.dao.RoleDao;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.model.Role;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Role Service Implementation
 */
@Service
@Transactional
public class RoleService extends BaseService<Role> {
    private PermissionDao permissionDao;

    public RoleService(RoleDao roleDao, PermissionDao permissionDao) {
        super(roleDao);
        this.permissionDao = permissionDao;
    }

    /**
     * Check for duplicates
     * @param role
     * @return
     */
    @Override
    protected boolean checkForDuplicates(Role role) {
        Role role1 = Role.builder()
                .name(role.getName())
                .build();

        Example<Role> example = Example.of(role1);
        Optional<Role> roleOptional = dao.findOne(example);
        return roleOptional.isPresent();
    }

    /**
     * Deletes the permission
     * @param uuid
     * @return
     */
    @Override
    public boolean delete(UUID uuid) {
        removeAllPermissions(uuid);
        super.delete(uuid);
        return true;
    }

    /**
     * Finds all permissions on a given role id
     * @param roleId
     * @return
     */
    public Set<Permission> findAllPermissions(@NotNull UUID roleId) {
        Role role = get(roleId);
        return role.getPermissions();
    }

    /**
     * Assign a permission to the given role
     * @param roleId
     * @param permission
     */
    public void assignPermission(@NotNull UUID roleId, @NotNull Permission permission) {
        Role role = get(roleId);
        permission.setRole(role);
        permission = permissionDao.save(permission);

        role.addPermission(permission);
        dao.save(role);
    }

    /**
     * Deletes a permission on a role
     * @param roleId
     * @param permissionId
     */
    public void deletePermission(@NotNull UUID roleId, @NotNull UUID permissionId) {
        Role role = get(roleId);
        Permission permission = permissionDao.getOne(permissionId);
        role.removePermission(permission);
        dao.save(role);
        permissionDao.delete(permission);
    }

    /**
     * Removes all permissions on a given role
     * @param roleId
     */
    public void removeAllPermissions(@NotNull UUID roleId) {
        Role role = get(roleId);
        Set<Permission> permissions = role.getPermissions();
        if (permissions != null) {
            permissions.forEach(permission -> permissionDao.delete(permission));
        }
        role.removeAllPermissions();
        dao.save(role);
    }
}
