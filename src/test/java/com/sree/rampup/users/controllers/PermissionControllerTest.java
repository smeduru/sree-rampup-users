package com.sree.rampup.users.controllers;

import com.sree.rampup.users.controller.PermissionController;
import com.sree.rampup.users.exception.EntityNotFoundException;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.service.PermissionService;
import com.sree.rampup.users.util.TestMockMvc;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sree.rampup.users.util.PermissionBuilder;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.sree.rampup.users.util.JsonMarshaller.parseJSON;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test cases for PermissionController
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PermissionController.class)
public class PermissionControllerTest {
    private static final String URI = "/v1/permission";
    private static final String ID = "id";

    // Could be replaced with UriTemplate or UriComponentBuilder
    private static final Function<UUID, String> pathVariantURI = (uuid) -> String.format("%s/%s", URI, uuid);

    @Autowired
    private MockMvc mockMvc;

    private TestMockMvc testMockMvc;

    @Before
    public void init() {
        testMockMvc = new TestMockMvc(mockMvc);
    }

    @MockBean
    PermissionService permissionService;

    // Get: Begin
    @Test
    public void getPermission_Invalid_UUID() throws Exception {
        UUID uuid = null;
        String url = pathVariantURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isBadRequest());
    }

    @Test
    public void getPermission_UUID_Success() throws Exception {
        UUID uuid = randomUUID();
        Permission mockPermission = PermissionBuilder.mockPermission();

        when(permissionService.get(uuid)).thenReturn(mockPermission);

        String url = pathVariantURI.apply(uuid);
        MvcResult mvcResult =  testMockMvc.get(url).thenValidate(status().isOk());
        Permission returnedPermission = parseJSON(mvcResult.getResponse().getContentAsString(), Permission.class);
        Assertions.assertThat(returnedPermission).isEqualToComparingFieldByField(mockPermission);
    }

    @Test
    public void findAllPermissions_Success() throws Exception {
        List<Permission> mockPermissions = PermissionBuilder.mockPermissionsForFindAll();

        when(permissionService.findAll()).thenReturn(mockPermissions);

        MvcResult mvcResult = testMockMvc.get(URI + "/").thenValidate(status().isOk());

        TypeReference<List<Permission>> typeReference = new TypeReference<List<Permission>>() { };

        List<Permission> returnedPermissionList = parseJSON(mvcResult.getResponse().getContentAsString(), typeReference);
        Assertions.assertThat(returnedPermissionList).hasSize(2).containsAll(mockPermissions);
    }

    @Test
    public void getPermission_UUID_PermissionNotFound() throws Exception {
        UUID uuid = randomUUID();
        when(permissionService.get(uuid)).thenThrow(new EntityNotFoundException());
        String url = pathVariantURI.apply(uuid);
        testMockMvc.get(url).thenValidate(status().isNotFound());
    }
    // Get: End

    // Update: Begin
    @Test
    public void enablePermission_UUID_PermissionNotFound() throws Exception {
        UUID permissionId = UUID.randomUUID();
        String url = pathVariantURI.apply(permissionId);
        Map<String, Boolean> enabledMap = new HashMap<>();
        enabledMap.put("enabled", true);

        doThrow(new EntityNotFoundException()).when(permissionService).enablePermission(permissionId, true);

        testMockMvc.put(url).withModel(enabledMap).thenValidate(status().isNotFound());
    }

    @Test
    public void  enablePermission_Success() throws Exception {
        UUID permissionId = UUID.randomUUID();
        String url = pathVariantURI.apply(permissionId);
        Map<String, Boolean> enabledMap = new HashMap<>();
        enabledMap.put("enabled", true);
        doNothing().when(permissionService).enablePermission(permissionId, true);
        testMockMvc.put(url).withModel(enabledMap).thenValidate(status().isOk());
    }
    // Update: End
}
