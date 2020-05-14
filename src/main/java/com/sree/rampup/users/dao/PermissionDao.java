package com.sree.rampup.users.dao;

import com.sree.rampup.users.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data Access Object for Permission
 */
@Repository
public interface PermissionDao extends JpaRepository<Permission, UUID> {

}
