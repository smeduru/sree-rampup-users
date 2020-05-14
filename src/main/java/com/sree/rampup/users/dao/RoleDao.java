package com.sree.rampup.users.dao;

import com.sree.rampup.users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data Access Object for Role
 */
@Repository
public interface RoleDao extends JpaRepository<Role, UUID> {

}
