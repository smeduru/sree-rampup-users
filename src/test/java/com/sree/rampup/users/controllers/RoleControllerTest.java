package com.sree.rampup.users.controllers;

import com.sree.rampup.users.controller.RoleController;
import com.sree.rampup.users.exception.EntityNotFoundException;

import com.sree.rampup.users.exception.EntityAlreadyExistsException;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.service.RoleService;
import com.sree.rampup.users.util.PermissionBuilder;
import com.sree.rampup.users.util.TestMockMvc;
import com.sree.rampup.users.util.RoleBuilder;
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
import static com.sree.rampup.users.util.RoleBuilder.*;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit test cases for RoleController
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RoleController.class)
public class RoleControllerTest {
    private static final String URI = "/v1/role";
    private static final String ID = "id";

    private static final String URI_PERMISSION = "permission";

    // Could be replaced with UriTemplate or UriComponentBuilder
    private static final Function<UUID, String> pathVariantURI = (uuid) -> String.format("%s/%s", URI, uuid);

    // Could be replaced with UriTemplate or UriComponentBuilder
    private static final BiFunction<UUID, UUID, String> pathVarRolePermissionURI
            = (roleId, permissionId) -> String.format("%s/%s/%s/%s",
                                                        URI,
                                                        (roleId == null) ? "" : roleId,
                                                        URI_PERMISSION,
                                                        (permissionId == null) ? "" : permissionId);


    @Autowired
    private MockMvc mockMvc;

    private TestMockMvc testMockMvc;

    @Before
    public void init() {
        testMockMvc = new TestMockMvc(mockMvc);
    }

    @MockBean
    RoleService roleService;

    //Add: Begin
    @Test
    public void addRole_Success() throws Exception {
        Role requestRole = RoleBuilder.mockRoleForAdd();

        Role mockRole = ObjectUtil.cloneObject(requestRole);
        UUID uuid = randomUUID();
        mockRole.setId(uuid);
        when(roleService.add(requestRole)).thenReturn(mockRole);

        MvcResult mvcResult = testMockMvc.post(URI).withModel(requestRole).thenValidate(status().isOk());

        Role returnedRole = parseJSON(mvcResult.getResponse().getContentAsString(), Role.class);

        Assertions.assertThat(returnedRole.getId()).isEqualTo(uuid);
        Assertions.assertThat(returnedRole).isEqualToIgnoringGivenFields(requestRole, ID);
    }

    @Test
    public void addRole_Empty_Name() throws Exception {
        Role role = RoleBuilder.mockRoleForAdd();
        role.setName("");
        testMockMvc.post(URI).withModel(role).thenValidate(status().isBadRequest());
    }

    @Test
    public void addRole_Invalid_Size_Name() throws Exception {
        Role role = RoleBuilder.mockRoleForAdd();
        role.setName("Long ROLE NAME >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        testMockMvc.post(URI).withModel(role).thenValidate(status().isBadRequest());
    }
    // Add: End

    // Get: Begin
    @Test
    public void getRole_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVariantURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void getRole_UUID_Success() throws Exception {
        UUID uuid = randomUUID();
        Role mockRole = getMockRole();

        when(roleService.get(uuid)).thenReturn(mockRole);

        String url = pathVariantURI.apply(uuid);
        MvcResult mvcResult =  testMockMvc.get(url).thenValidate(status().isOk());
        Role returnedRole = parseJSON(mvcResult.getResponse().getContentAsString(), Role.class);
        Assertions.assertThat(returnedRole).isEqualToComparingFieldByField(mockRole);
    }

    @Test
    public void findAllRoles_Success() throws Exception {
        List<Role> mockRoles = mockRolesForFindAll();

        when(roleService.findAll()).thenReturn(mockRoles);

        MvcResult mvcResult = testMockMvc.get(URI + "/").thenValidate(status().isOk());

        TypeReference<List<Role>> typeReference = new TypeReference<List<Role>>() { };

        List<Role> returnedRoleList = parseJSON(mvcResult.getResponse().getContentAsString(), typeReference);
        Assertions.assertThat(returnedRoleList).hasSize(2).containsAll(mockRoles);
    }

    @Test
    public void getRole_UUID_RoleNotFound() throws Exception {
        UUID uuid = randomUUID();
        when(roleService.get(uuid)).thenThrow(new EntityNotFoundException());
        String url = pathVariantURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isNotFound());
    }
    // Get: End

    // Update: Begin
    @Test
    public void updateRole_Success() throws Exception {
        Role role = RoleBuilder.getMockRole();
        String url = pathVariantURI.apply(role.getId());
        testMockMvc.put(url).withModel(role).thenValidate(status().isOk());
    }

    @Test
    public void updateRole_UUID_RoleNotFound() throws Exception {
        Role role = RoleBuilder.getMockRole();
        when(roleService.update(role)).thenThrow(new EntityNotFoundException());
        String url = pathVariantURI.apply(role.getId());
        testMockMvc.put(url).withModel(role).thenValidate(status().isNotFound());
    }

    @Test
    public void updateRole_UUID_DuplicateRoleFound() throws Exception {
        Role role = RoleBuilder.getMockRole();
        when(roleService.update(role)).thenThrow(new EntityAlreadyExistsException());
        String url = pathVariantURI.apply(role.getId());
        testMockMvc.put(url).withModel(role).thenValidate(status().isConflict());
    }

    @Test
    public void updateRole_Empty_UUId() throws Exception {
        Role role = RoleBuilder.getMockRole();
        role.setId(null);
        String url = pathVariantURI.apply(role.getId());
        testMockMvc.put(url).withModel(role).thenValidate(status().isBadRequest());
    }

    @Test
    public void updateRole_Empty_FirstName() throws Exception {
        Role role = RoleBuilder.mockRoleForAdd();
        role.setName("");
        String url = pathVariantURI.apply(role.getId());
        testMockMvc.put(url).withModel(role).thenValidate(status().isBadRequest());
    }
    // Update: End

    // Delete: Begin
    @Test
    public void deleteRole_Valid_UUID() throws Exception {
        UUID uuid = randomUUID();
        String url = pathVariantURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isOk());
    }

    @Test
    public void deleteRole_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVariantURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void deleteRole_UUID_RoleNotFound() throws Exception {
        UUID uuid = randomUUID();
        doThrow(new EntityNotFoundException()).when(roleService).delete(uuid);
        String url = pathVariantURI.apply(uuid);
        testMockMvc.delete(url).thenValidate(status().isNotFound());
    }
    // Delete: End

    //Permissions: Begin
    @Test
    public void assignPermission_Success() throws Exception{
        UUID roleId = UUID.randomUUID();
        Permission permission = PermissionBuilder.mockPermission();

        doNothing().when(roleService).assignPermission(roleId, permission);

        String url = pathVarRolePermissionURI.apply(roleId, null);
        testMockMvc.put(url).withModel(permission).thenValidate(status().isOk());
    }

    @Test
    public void deletePermission_Success() throws Exception {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        doNothing().when(roleService).deletePermission(roleId, permissionId);

        String url = pathVarRolePermissionURI.apply(roleId, permissionId);
        testMockMvc.delete(url).thenValidate(status().isOk());
    }

    @Test
    public void findAllPermissions_Success() throws Exception {
        UUID roleId = UUID.randomUUID();
        Set<Permission> permissions = PermissionBuilder.mockPermissions();

        when(roleService.findAllPermissions(roleId)).thenReturn(permissions);

        String url = pathVarRolePermissionURI.apply(roleId, null);
        MvcResult mvcResult = testMockMvc.get(url).thenValidate(status().isOk());

        TypeReference<Set<Permission>> typeReference = new TypeReference<Set<Permission>>() { };

        Set<Permission> respPermissions = parseJSON(mvcResult.getResponse().getContentAsString(), typeReference);

        Assertions.assertThat(respPermissions).hasSize(2).containsAll(permissions);
    }

    @Test
    public void removePermission_Success() throws Exception {
        UUID roleId = UUID.randomUUID();

        doNothing().when(roleService).removeAllPermissions(roleId);

        String url = pathVarRolePermissionURI.apply(roleId, null);
        testMockMvc.delete(url).thenValidate(status().isOk());
    }
}
