package com.sree.rampup.users.dao;

import com.sree.rampup.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data Access Object for User
 */
@Repository
public interface UserDao extends JpaRepository<User, UUID> {

}
