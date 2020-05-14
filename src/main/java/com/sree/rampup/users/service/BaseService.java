package com.sree.rampup.users.service;

import com.sree.rampup.users.exception.EntityNotFoundException;

import com.sree.rampup.users.exception.EntityAlreadyExistsException;
import com.sree.rampup.users.model.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Base service
 * @param <T>
 */
public abstract class BaseService<T extends Identifier> {
    protected JpaRepository<T, UUID> dao;

    public BaseService(JpaRepository<T, UUID> dao) {
        this.dao = dao;
    }

    /**
     * Add a record
     * @param domain
     * @return
     */
    public T add(T domain) {
        if (checkForDuplicates(domain)) {
            throw new EntityAlreadyExistsException();
        }

        return dao.save(domain);
    }

    /**
     * Retrieve a record
     * @param uuid
     * @return
     */
    public T get(UUID uuid) {
        Optional<T> optional = dao.findById(uuid);
        if (!optional.isPresent()) {
            throw new EntityNotFoundException();
        }
        return optional.get();
    }

    /**
     * Deletes a record
     * @param uuid
     * @return
     */
    public boolean delete(UUID uuid) {
        boolean exists = dao.existsById(uuid);
        if (!exists) {
            throw new EntityNotFoundException();
        }
        dao.deleteById(uuid);
        return exists;
    }

    /**
     * Updates the given record
     * @param domain
     * @return
     */
    public boolean update(T domain) {
        boolean exists = dao.existsById(domain.getId());
        if (!exists) {
            throw new EntityNotFoundException();
        }
        if (checkForDuplicates(domain)) {
            throw new EntityAlreadyExistsException();
        }
        dao.save(domain);
        return true;
    }

    /**
     * Find all records
     * @return
     */
    public List<T> findAll() {
        List<T> domainList = dao.findAll();
        return domainList;
    }

    /**
     * Abstract method for checking for duplicates based on the business key
     * @param domain
     * @return
     */
    protected abstract boolean checkForDuplicates(T domain);
}
