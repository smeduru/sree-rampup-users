package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.PermissionDao;
import com.sree.rampup.users.dao.RoleDao;
import com.sree.rampup.users.exception.EntityNotFoundException;

import com.sree.rampup.users.exception.EntityAlreadyExistsException;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.util.PermissionBuilder;
import com.sree.rampup.users.util.RoleBuilder;
import com.sree.rampup.users.util.ObjectUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Unit test cases for RoleService
 */
@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {
    @Mock
    private RoleDao roleDao;

    @Mock
    private PermissionDao permissionDao;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void addRole_valid_role() throws InstantiationException, IllegalAccessException {
        Role roleRequest = RoleBuilder.mockRoleForAdd();

        Role mockRole = ObjectUtil.cloneObject(roleRequest);
        UUID uuid = randomUUID();
        mockRole.setId(uuid);

        Optional<Role> roleOptional = Optional.empty();

        given(roleDao.findOne(any())).willReturn(roleOptional);
        given(roleDao.save(roleRequest)).willReturn(mockRole);
        Role returnedRole = roleService.add(roleRequest);

        then(roleDao).should().findOne(any());
        then(roleDao).should().save(roleRequest);
        Assertions.assertThat(returnedRole).isEqualToComparingFieldByField(mockRole);
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void addRole_duplicate_role() {
        Role roleRequest = RoleBuilder.mockRoleForAdd();

        Optional<Role> roleOptional = Optional.of(roleRequest);

        given(roleDao.findOne(any())).willReturn(roleOptional);
        roleService.add(roleRequest);

        then(roleDao).should().findOne(any());
    }

    @Test
    public void getRole_valid_UUID() {
        Role mockRole = RoleBuilder.getMockRole();
        Optional<Role> optional = Optional.of(mockRole);
        UUID uuid = mockRole.getId();

        given(roleDao.findById(uuid)).willReturn(optional);

        Role returnedRole = roleService.get(uuid);

        then(roleDao).should().findById(uuid);
        Assertions.assertThat(returnedRole).isEqualToComparingFieldByField(mockRole);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getRole_invalid_UUID() {
        UUID uuid = randomUUID();
        Optional<Role> optional = Optional.empty();

        given(roleDao.findById(uuid)).willReturn(optional);

        roleService.get(uuid);
        then(roleDao).should().findById(uuid);
    }

    @Test
    public void deleteRole_valid_role() {
        Role role = RoleBuilder.getMockRole();
        UUID uuid = role.getId();

        Optional<Role> optional = Optional.of(role);
        given(roleDao.findById(uuid)).willReturn(optional);
        given(roleDao.existsById(uuid)).willReturn(true);
        boolean deletedFlag = roleService.delete(uuid);

        then(roleDao).should().existsById(uuid);
        Assertions.assertThat(deletedFlag).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteRole_invalid_role() {
        UUID uuid = randomUUID();
        roleService.delete(uuid);
        then(roleDao).should().existsById(uuid);
    }

    @Test
    public void updateRole_valid_role() {
        Role mockRoleForUpdate = RoleBuilder.getMockRole();
        Role mockRoleForCheck = RoleBuilder.getMockRole();
        mockRoleForCheck.setId(null);

        Optional<Role> roleOptional = Optional.empty();

        given(roleDao.existsById(any())).willReturn(true);
        given(roleDao.findOne(any())).willReturn(roleOptional);
        given(roleDao.save(mockRoleForUpdate)).willReturn(mockRoleForUpdate);
        boolean isSuccess = roleService.update(mockRoleForUpdate);

        then(roleDao).should().existsById(any());
        then(roleDao).should().findOne(any());
        then(roleDao).should().save(mockRoleForUpdate);
        Assertions.assertThat(isSuccess).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateRole_role_not_found() {
        Role mockRole = RoleBuilder.getMockRole();
        Example<Role> example = Example.of(mockRole);

        given(roleDao.existsById(any())).willReturn(false);
        roleService.update(mockRole);

        then(roleDao).should().findOne(example);
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void updateRole_role_already_exists() {
        Role mockRole = RoleBuilder.getMockRole();

        given(roleDao.existsById(any())).willReturn(true);

        Optional<Role> roleOptional = Optional.of(mockRole);
        given(roleDao.findOne(any())).willReturn(roleOptional);
        roleService.update(mockRole);

        then(roleDao).should().findOne(any());
    }

    @Test
    public void findAllPermissions_success() {
        UUID roleId = UUID.randomUUID();
        Set<Permission> permissions = PermissionBuilder.mockPermissions();
        Role role = RoleBuilder.getMockRole();
        role.setPermissions(permissions);

        Optional<Role> optionalRole = Optional.of(role);
        given(roleDao.findById(roleId)).willReturn(optionalRole);

        Set<Permission> returnedPermissions = roleService.findAllPermissions(roleId);
        Assertions.assertThat(returnedPermissions).isEqualTo(permissions);
    }

    @Test
    public void assignPermission_success() {
        UUID roleId = UUID.randomUUID();
        Permission permission = PermissionBuilder.mockPermissionForAdd();

        Role role = RoleBuilder.getMockRole();
        Optional<Role> optionalRole = Optional.of(role);
        given(roleDao.findById(roleId)).willReturn(optionalRole);

        permission.setRole(role);
        given(permissionDao.save(permission)).willReturn(permission);

        role.addPermission(permission);
        given(roleDao.save(role)).willReturn(role);

        roleService.assignPermission(roleId, permission);
    }

    @Test
    public void deletePermission_Success() {
        UUID roleId = UUID.randomUUID();
        Role role = RoleBuilder.getMockRole();
        Optional<Role> optionalRole = Optional.of(role);
        given(roleDao.findById(roleId)).willReturn(optionalRole);

        Permission permission = PermissionBuilder.mockPermission();
        UUID permissionId = permission.getId();
        permission.setRole(role);
        given(permissionDao.getOne(permissionId)).willReturn(permission);

        roleService.deletePermission(roleId, permissionId);
    }

    @Test
    public void removeAllPermissions_Success() {
        UUID roleId = UUID.randomUUID();
        Role role = RoleBuilder.getMockRole();
        Optional<Role> optionalRole = Optional.of(role);
        given(roleDao.findById(roleId)).willReturn(optionalRole);

        roleService.removeAllPermissions(roleId);
    }
}
