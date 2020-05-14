package com.sree.rampup.users.controllers;

import com.sree.rampup.users.controller.UserController;
import com.sree.rampup.users.exception.EntityNotFoundException;

import com.sree.rampup.users.exception.EntityAlreadyExistsException;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;
import com.sree.rampup.users.service.UserService;
import com.sree.rampup.users.util.RoleBuilder;
import com.sree.rampup.users.util.TestMockMvc;
import com.sree.rampup.users.util.UserBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sree.rampup.users.util.ObjectUtil;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.sree.rampup.users.util.JsonMarshaller.parseJSON;
import static com.sree.rampup.users.util.UserBuilder.*;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test cases for UserController
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String URI_USER = "/v1/user";
    private static final String URI_ROLE = "role";

    private static final String ID = "id";

    // Could be replaced with UriTemplate or UriComponentBuilder
    private static final Function<UUID, String> pathVarUserURI = (uuid) -> String.format("%s/%s", URI_USER, uuid);

    // Could be replaced with UriTemplate or UriComponentBuilder
    private static final BiFunction<UUID, UUID, String> pathVarUserRoleURI
            = (userId, roleId) -> String.format("%s/%s/%s/%s", URI_USER,
                                                               (userId == null) ? "" : userId,
                                                               URI_ROLE,
                                                               (roleId == null) ? "" : roleId);

    @Autowired
    private MockMvc mockMvc;

    private TestMockMvc testMockMvc;

    @Before
    public void init() {
        testMockMvc = new TestMockMvc(mockMvc);
    }

    @MockBean
    UserService userService;

    //Add: Begin
    @Test
    public void addUser_Success() throws Exception {
        User requestUser = UserBuilder.mockUserForAdd();

        User mockUser = ObjectUtil.cloneObject(requestUser);
        UUID uuid = randomUUID();
        mockUser.setId(uuid);
        when(userService.add(requestUser)).thenReturn(mockUser);

        MvcResult mvcResult = testMockMvc.post(URI_USER).withModel(requestUser).thenValidate(status().isOk());

        User returnedUser = parseJSON(mvcResult.getResponse().getContentAsString(), User.class);

        Assertions.assertThat(returnedUser.getId()).isEqualTo(uuid);
        Assertions.assertThat(returnedUser).isEqualToIgnoringGivenFields(requestUser, ID);
    }

    @Test
    public void addUser_Empty_FirstName() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setFirstName("");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void addUser_Invalid_Size_FirstName() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setFirstName("Long First Name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void addUser_Empty_LastName() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setLastName("");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void addUser_Invalid_Size_LastName() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setLastName("Long Last Name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void addUser_Empty_Email() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setEmail("");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void addUser_Invalid_Size_Email() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setEmail("Long.Email.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>@test.com");
        testMockMvc.post(URI_USER).withModel(user).thenValidate(status().isBadRequest());
    }
    // Add: End

    // Get: Begin
    @Test
    public void getUser_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVarUserURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void getUser_UUID_Success() throws Exception {
        UUID uuid = randomUUID();
        User mockUser = getMockUser();

        when(userService.get(uuid)).thenReturn(mockUser);

        String url = pathVarUserURI.apply(uuid);
        MvcResult mvcResult =  testMockMvc.get(url).thenValidate(status().isOk());
        User returnedUser = parseJSON(mvcResult.getResponse().getContentAsString(), User.class);
        Assertions.assertThat(returnedUser).isEqualToComparingFieldByField(mockUser);
    }

    @Test
    public void findAllUsers_Success() throws Exception {
        List<User> mockUsers = mockUsersForFindAll();

        when(userService.findAll()).thenReturn(mockUsers);

        MvcResult mvcResult = testMockMvc.get(URI_USER + "/").thenValidate(status().isOk());

        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() { };

        List<User> returnedUserList = parseJSON(mvcResult.getResponse().getContentAsString(), typeReference);
        Assertions.assertThat(returnedUserList).hasSize(2).containsAll(mockUsers);
    }

    @Test
    public void getUser_UUID_UserNotFound() throws Exception {
        UUID uuid = randomUUID();
        when(userService.get(uuid)).thenThrow(new EntityNotFoundException());
        String url = pathVarUserURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isNotFound());
    }
    // Get: End

    // Update: Begin
    @Test
    public void updateUser_Success() throws Exception {
        User user = UserBuilder.getMockUser();
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isOk());
    }

    @Test
    public void updateUser_UUID_UserNotFound() throws Exception {
        User user = UserBuilder.getMockUser();
        doThrow(new EntityNotFoundException()).when(userService).update(any());
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isNotFound());
    }

    @Test
    public void updateUser_UUID_DuplicateUserFound() throws Exception {
        User user = UserBuilder.getMockUser();
        when(userService.update(user)).thenThrow(new EntityAlreadyExistsException());
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isConflict());
    }

    @Test
    public void updateUser_Empty_UUId() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setId(null);
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Empty_FirstName() throws Exception {
        User user = UserBuilder.mockUserForAdd();
        user.setFirstName("");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Invalid_Size_FirstName() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setFirstName("Long First Name>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Empty_LastName() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setLastName("");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Invalid_Size_LastName() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setLastName("Long Last Name>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Empty_Email() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setEmail("");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateUser_Invalid_Size_Email() throws Exception {
        User user = UserBuilder.getMockUser();
        user.setEmail("Long.Email.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>@test.com");
        String url = pathVarUserURI.apply(user.getId());
        testMockMvc.put(url).withModel(user).thenValidate(status().isBadRequest());
    }
    // Update: End

    // Delete: Begin
    @Test
    public void deleteUser_Valid_UUID() throws Exception {
        UUID uuid = randomUUID();
        String url = pathVarUserURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isOk());
    }

    @Test
    public void deleteUser_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVarUserURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void deleteUser_UUID_UserNotFound() throws Exception {
        UUID uuid = randomUUID();
        doThrow(new EntityNotFoundException()).when(userService).delete(uuid);
        String url = pathVarUserURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isNotFound());
    }
    // Delete: End

    // Roles: Begin
    @Test
    public void findAllRoles_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVarUserRoleURI.apply(uuid, null);
        testMockMvc.get(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void findAllRoles_UUID_NotFound() throws Exception {
        UUID uuid = randomUUID();
        doThrow(new EntityNotFoundException()).when(userService).findAllRoles(uuid);
        String url = pathVarUserRoleURI.apply(uuid, null);
        testMockMvc.get(url).thenValidate(status().isNotFound());
    }

    @Test
    public void findAllRoles_success() throws Exception {
        //Set<Role> roles = userService.findAllRoles(userId);
        UUID uuid = UUID.randomUUID();
        Set<Role> mockRoles = mockFindAllRolesForUserId();

        when(userService.findAllRoles(uuid)).thenReturn(mockRoles);

        MvcResult mvcResult = testMockMvc.get(pathVarUserRoleURI.apply(uuid, null)).thenValidate(status().isOk());

        TypeReference<Set<Role>> typeReference = new TypeReference<Set<Role>>() { };

        Set<Role> returnedUserList = parseJSON(mvcResult.getResponse().getContentAsString(), typeReference);
        Assertions.assertThat(returnedUserList).hasSize(2).containsAll(mockRoles);
    }

    @Test
    public void findRole_by_UserId_RoleId() throws Exception {
        UUID userId = UUID.randomUUID();

        Role mockRole = RoleBuilder.getMockRole(userId);
        given(userService.findRole(userId, mockRole.getId())).willReturn(mockRole);

        String url = pathVarUserRoleURI.apply(userId, mockRole.getId());
        MvcResult mvcResult = testMockMvc.get(url).thenValidate(status().isOk());

        Role returnedRole = parseJSON(mvcResult.getResponse().getContentAsString(), Role.class);
        Assertions.assertThat(returnedRole).isNotNull().extracting("id").containsExactly(mockRole.getId());

    }

    @Test
    public void findRole_by_UserId_RoleId_EntityNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        given(userService.findRole(userId, roleId)).willThrow(new EntityNotFoundException());

        String url = pathVarUserRoleURI.apply(userId, roleId);
        testMockMvc.get(url).thenValidate(status().isNotFound());
    }

    @Test
    public void assignRole_UserId_RoleId() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        doNothing().when(userService).assignRole(userId, roleId);

        String url = pathVarUserRoleURI.apply(userId, roleId);
        testMockMvc.put(url).withModel(null).thenValidate(status().isOk());
    }

    @Test
    public void assignRole_UserId_RoleId_EntityNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        doThrow(new EntityNotFoundException()).when(userService).assignRole(userId, roleId);

        String url = pathVarUserRoleURI.apply(userId, roleId);
        testMockMvc.put(url).withModel(null).thenValidate(status().isNotFound());
    }

    @Test
    public void removeRole_UserId_RoleId() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        doNothing().when(userService).removeRole(userId, roleId);

        String url = pathVarUserRoleURI.apply(userId, roleId);
        testMockMvc.delete(url).thenValidate(status().isOk());
    }

    @Test
    public void removeRole_UserId_RoleId_EntityNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        doThrow(new EntityNotFoundException()).when(userService).removeRole(userId, roleId);

        String url = pathVarUserRoleURI.apply(userId, roleId);
        testMockMvc.delete(url).thenValidate(status().isNotFound());
    }
    // Roles: End
}
