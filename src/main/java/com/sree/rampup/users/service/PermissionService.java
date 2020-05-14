package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.PermissionDao;
import com.sree.rampup.users.exception.EntityNotFoundException;
import com.sree.rampup.users.model.Permission;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Permission Service Implementation
 */
@Service
@Transactional
public class PermissionService extends BaseService<Permission> {

    public PermissionService(PermissionDao permissionDao) {
        super(permissionDao);
    }

    /**
     * Check for duplicates.
     * @param permission
     * @return
     */
    @Override
    protected boolean checkForDuplicates(Permission permission) {
        return true;
    }

    /**
     * Enables/Disables the permission
     * @param uuid
     * @param enabled
     */
    public void enablePermission(UUID uuid, boolean enabled) {
        Optional<Permission> optional = dao.findById(uuid);
        if (!optional.isPresent()) {
            throw new EntityNotFoundException();
        }
        Permission permission = optional.get();
        permission.setEnabled(enabled);
        dao.save(permission);
    }
}
