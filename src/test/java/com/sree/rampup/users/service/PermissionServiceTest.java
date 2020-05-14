package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.PermissionDao;
import com.sree.rampup.users.exception.EntityNotFoundException;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.util.PermissionBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

/**
 * Unit test cases for PermissionService
 */
@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {
    @Mock
    private PermissionDao permissionDao;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    public void getPermission_valid_UUID() {
        Permission mockPermission = PermissionBuilder.mockPermission();
        Optional<Permission> optional = Optional.of(mockPermission);
        UUID uuid = mockPermission.getId();

        given(permissionDao.findById(uuid)).willReturn(optional);

        Permission returnedPermission = permissionService.get(uuid);

        then(permissionDao).should().findById(uuid);
        Assertions.assertThat(returnedPermission).isEqualToComparingFieldByField(mockPermission);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getPermission_invalid_UUID() {
        UUID uuid = randomUUID();
        Optional<Permission> optional = Optional.empty();

        given(permissionDao.findById(uuid)).willReturn(optional);

        permissionService.get(uuid);
        then(permissionDao).should().findById(uuid);
    }

    @Test
    public void enablePermission_success() {
        Permission mockPermission = PermissionBuilder.mockPermission();
        UUID permissionId = mockPermission.getId();
        mockPermission.setEnabled(false);

        Optional<Permission> optional = Optional.of(mockPermission);

        given(permissionDao.findById(permissionId)).willReturn(optional);
        given(permissionDao.save(mockPermission)).willReturn(mockPermission);
        permissionService.enablePermission(permissionId, false);

        then(permissionDao).should().findById(permissionId);
        then(permissionDao).should().save(mockPermission);
    }

    @Test(expected = EntityNotFoundException.class)
    public void enablePermission_entity_not_found() {
        Permission mockPermission = PermissionBuilder.mockPermission();
        UUID permissionId = mockPermission.getId();
        mockPermission.setEnabled(false);

        Optional<Permission> optional = Optional.empty();

        given(permissionDao.findById(permissionId)).willReturn(optional);

        permissionService.enablePermission(permissionId, false);

        then(permissionDao).should().findById(permissionId);
    }

    @Test
    public void findAllPermissions_success() {
        List<Permission> permissions = PermissionBuilder.mockPermissionsForFindAll();

        given(permissionDao.findAll()).willReturn(permissions);

        List<Permission> returnedPermissions = permissionService.findAll();
        Assertions.assertThat(returnedPermissions).isEqualTo(permissions);
    }
}
