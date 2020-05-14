package com.sree.rampup.users.service;

import com.sree.rampup.users.dao.RoleDao;
import com.sree.rampup.users.dao.UserDao;
import com.sree.rampup.users.exception.EntityNotFoundException;

import com.sree.rampup.users.exception.EntityAlreadyExistsException;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;
import com.sree.rampup.users.util.UserBuilder;
import com.sree.rampup.users.util.ObjectUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.sree.rampup.users.util.RoleBuilder.getMockRole;
import static com.sree.rampup.users.util.UserBuilder.getMockUser;
import static com.sree.rampup.users.util.UserBuilder.mockFindAllRolesForUserId;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Unit test cases for UserService
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private UserService userService;

    @Test
    public void addUser_valid_user() throws InstantiationException, IllegalAccessException {
        User userRequest = UserBuilder.mockUserForAdd();

        User mockUser = ObjectUtil.cloneObject(userRequest);
        mockUser.setId(randomUUID());

        Optional<User> userOptional = Optional.empty();

        given(userDao.findOne(any())).willReturn(userOptional);
        given(userDao.save(userRequest)).willReturn(mockUser);
        User returnedUser = userService.add(userRequest);

        then(userDao).should().findOne(any());
        then(userDao).should().save(userRequest);
        Assertions.assertThat(returnedUser).isEqualToComparingFieldByField(mockUser);
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void addUser_duplicate_user() {
        User userRequest = UserBuilder.mockUserForAdd();

        Optional<User> userOptional = Optional.of(userRequest);

        given(userDao.findOne(any())).willReturn(userOptional);
        userService.add(userRequest);

        then(userDao).should().findOne(any());
    }

    @Test
    public void getUser_valid_UUID() {
        User mockUser = UserBuilder.getMockUser();
        Optional<User> optional = Optional.of(mockUser);
        UUID uuid = mockUser.getId();

        given(userDao.findById(uuid)).willReturn(optional);

        User returnedUser = userService.get(uuid);

        then(userDao).should().findById(uuid);
        Assertions.assertThat(returnedUser).isEqualToComparingFieldByField(mockUser);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getUser_invalid_UUID() {
        UUID uuid = randomUUID();
        Optional<User> optional = Optional.empty();

        given(userDao.findById(uuid)).willReturn(optional);

        userService.get(uuid);
        then(userDao).should().findById(uuid);
    }

    @Test
    public void deleteUser_valid_user() {
        UUID uuid = randomUUID();

        given(userDao.existsById(uuid)).willReturn(true);
        boolean deletedFlag = userService.delete(uuid);

        then(userDao).should().existsById(uuid);
        Assertions.assertThat(deletedFlag).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteUser_invalid_user() {
        UUID uuid = randomUUID();

        given(userDao.existsById(uuid)).willReturn(false);
        userService.delete(uuid);
        then(userDao).should().existsById(uuid);
    }

    @Test
    public void updateUser_valid_user() {
        User mockUserForUpdate = UserBuilder.getMockUser();
        User mockUserForCheck = UserBuilder.getMockUser();
        mockUserForCheck.setId(null);

        Optional<User> userOptional = Optional.empty();

        given(userDao.existsById(any())).willReturn(true);
        given(userDao.findOne(any())).willReturn(userOptional);
        given(userDao.save(mockUserForUpdate)).willReturn(mockUserForUpdate);
        boolean isSuccess = userService.update(mockUserForUpdate);

        then(userDao).should().existsById(any());
        then(userDao).should().findOne(any());
        then(userDao).should().save(mockUserForUpdate);
        Assertions.assertThat(isSuccess).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateUser_user_not_found() {
        User mockUser = UserBuilder.getMockUser();
        Example<User> example = Example.of(mockUser);

        given(userDao.existsById(any())).willReturn(false);
        userService.update(mockUser);

        then(userDao).should().findOne(example);
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void updateUser_user_already_exists() {
        User mockUser = UserBuilder.getMockUser();

        given(userDao.existsById(any())).willReturn(true);

        Optional<User> userOptional = Optional.of(mockUser);
        given(userDao.findOne(any())).willReturn(userOptional);
        userService.update(mockUser);

        then(userDao).should().findOne(any());
    }

    //Roles: Begin
    @Test
    public void findAllRoles() {
        UUID userId = UUID.randomUUID();
        User user = getMockUser();
        Set<Role> mockRoles = mockFindAllRolesForUserId();
        user.setRoles(mockRoles);
        Optional<User> optional = Optional.of(user);
        given(userDao.findById(userId)).willReturn(optional);

        Set<Role> roles = userService.findAllRoles(userId);
        Assertions.assertThat(roles).isEqualTo(mockRoles);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findAllRoles_Exception() {
        UUID userId = UUID.randomUUID();
        given(userDao.findById(userId)).willThrow(new EntityNotFoundException());

        userService.findAllRoles(userId);
    }

    @Test
    public void findRole_by_UserId_RoleId() {
        User user = getMockUser();
        Set<Role> mockRoles = mockFindAllRolesForUserId();
        user.setRoles(mockRoles);

        UUID userId = user.getId();

        Optional<User> optionalUser = Optional.of(user);
        given(userDao.findById(userId)).willReturn(optionalUser);

        Role mockRole = mockRoles.iterator().next();
//        given(roleDao.getOne(mockRole.getId())).willReturn(mockRole);

        Role returnedRole = userService.findRole(userId, mockRole.getId());
        Assertions.assertThat(returnedRole).isEqualTo(mockRole);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findRole_Exception() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        // Default mocks return null. So no need to mock.
        userService.findRole(userId, roleId);
    }

    @Test
    public void assignRole_by_UserId_RoleId() {
        User user = getMockUser();
        Role role = getMockRole();

        given(userDao.getOne(user.getId())).willReturn(user);
        given(roleDao.getOne(role.getId())).willReturn(role);

        userService.assignRole(user.getId(), role.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void assignRole_by_UserId_RoleId_Exception() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        // Default mocks return null. So no need to mock.
        userService.assignRole(userId, roleId);
    }
    //Roles: End
}
