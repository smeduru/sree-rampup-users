package com.sree.rampup.users.integrationtest.steps;

import com.sree.rampup.users.integrationtest.context.ScenarioContext;
import com.sree.rampup.users.model.Permission;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.util.JsonMarshaller;
import com.fasterxml.jackson.core.type.TypeReference;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.sree.rampup.users.util.JsonMarshaller.parseJSON;

@RequiredArgsConstructor
public class RoleSteps {
    public static final String URI ="/v1/role";
    public static final String URI_WITH_ID = URI + "/{uuid}";
    public static final String URI_WITH_PERMISSION = URI_WITH_ID + "/permission";
    public static final String URI_WITH_PERMISSION_ID = URI_WITH_PERMISSION + "/{permissionid}";

    private ResponseEntity<Role> roleResponseEntity;
    private ResponseEntity<String> stringResponseEntity;
    private ResponseEntity<Void> voidResponseEntity;

    private Role role;
    private UUID invalidRoleId;
    private String permissionToAdd;

    private final ScenarioContext scenarioContext;

    private final TestRestTemplate restTemplate;

    @Given("role for add: (.*)$")
    public void roleForAddRoleName(String roleName) {
        role =  Role.builder()
                .name(roleName)
                .build();
    }

    @When("^client request POST /role$")
    public void clientRequestPOSTRole() {
        roleResponseEntity = restTemplate.postForEntity(URI, role, Role.class);
    }

    @Then("role endpoint should respond with http status code (\\d+)$")
    public void roleEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(roleResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the role response should contain valid UUID")
    public void theRoleResponseShouldContainValidUUID() {
        Assertions.assertThat(roleResponseEntity.getBody().getId()).isNotNull();
        if (scenarioContext.getRoleUUID() == null) {
            scenarioContext.setRoleUUID(roleResponseEntity.getBody().getId());
        }
    }

    @Given("system knows about the role: (.+)")
    public void systemKnowsAboutTheRole(String roleName) {
        Assertions.assertThat(scenarioContext.getRoleUUID()).isNotNull();
    }

    @When("client request GET /role/(.*)$")
    public void clientRequestGETRoleRoleid(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) { // findAll
            stringResponseEntity = restTemplate.getForEntity(URI + "/", String.class);
        }
        else if ("roleid".equals(uuidStr)) { // UUID interpolation
            UUID uuid = scenarioContext.getRoleUUID();
            roleResponseEntity = restTemplate.getForEntity(URI_WITH_ID, Role.class, uuid);
        }
        else { // Actual UUID
            UUID uuidParam = UUID.fromString(uuidStr);
            roleResponseEntity = restTemplate.getForEntity(URI_WITH_ID, Role.class, uuidParam);
        }
    }

    @And("the role response should be JSON:$")
    public void theRoleResponseShouldBeJSON(String json) {
        Role responseRole = roleResponseEntity.getBody();
        try {
            Role expectedRole = JsonMarshaller.parseJSON(json, Role.class);
            Assertions.assertThat(responseRole.getId()).isEqualTo(scenarioContext.getRoleUUID());
            Assertions.assertThat(responseRole.getName()).isEqualTo(expectedRole.getName());
            if (permissionToAdd != null) {
                Permission permission = responseRole.getPermissions().iterator().next();
                scenarioContext.setPermissionUUID(permission.getId());
                Assertions.assertThat(responseRole.getPermissions()).first()
                        .extracting("name", "enabled")
                        .contains(permissionToAdd, true);
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Given("invalid role id (.+)$")
    public void invalidRoleIdBEbCBFB(String roleId) {
        //Do nothing
    }

    @Given("System knows about GOOGLE_ADMIN_ROLE, ADMIN_ROLE, APP_USER_ROLE")
    public void systemKnowsAboutADMIN_ROLEAPP_USER_ROLE() {
        // Do nothing
    }

    @Then("findAll role endpoint should respond with http status code (\\d+)$")
    public void findallRoleEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(stringResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("findAll role size is (\\d+) and response should be JSON:$")
    public void findallRoleSizeIsAndResponseShouldBeJSON(int size, String json) {
        TypeReference<List<Role>> typeReference = new TypeReference<List<Role>>() { };
        try {
            List<Role> returnedRoleList = parseJSON(stringResponseEntity.getBody(), typeReference);
            List<Role> expectedRoleList = parseJSON(json, typeReference);
            Assertions.assertThat(returnedRoleList.size()).isEqualTo(size);
            Role role1 = returnedRoleList.get(0);
            Role role2 = returnedRoleList.get(1);
            Role role3 = returnedRoleList.get(2);

            Assertions.assertThat(expectedRoleList).extracting("name")
                    .containsExactly(role1.getName(), role2.getName(), role3.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Exception during JSON Parsing: \n%s \n%s", stringResponseEntity.getBody(), json);
        }
    }

    @When("client request PUT /role/(.*)")
    public void clientRequestPUTRoleRoleid(String roleIdStr) {
        if (!"roleId".equals(roleIdStr)) { // Invalid scenario
            invalidRoleId = UUID.fromString(roleIdStr);
        }
    }

    @And("update role with {word}")
    public void updateRoleWithName(String roleName) {
        Role role = Role.builder()
                .name(roleName)
                .build();
        if (invalidRoleId != null) {
            role.setId(invalidRoleId);
        }
        else {
            role.setId(scenarioContext.getRoleUUID());
        }

        HttpEntity<Role> httpRole = new HttpEntity<>(role);
        roleResponseEntity = restTemplate.exchange(URI_WITH_ID, HttpMethod.PUT, httpRole, Role.class, role.getId());
    }

    @And("role read should retrieve the updates")
    public void roleReadShouldRetrieveTheUpdates() {
        clientRequestGETRoleRoleid(scenarioContext.getRoleUUID().toString());
    }

    @When("role client request DELETE /role/(.+)$")
    public void clientRequestDELETERoleRoleId(String roleIdStr) {
        UUID uuid = null;
        if ("roleId".equals(roleIdStr)) {
            uuid = scenarioContext.getRoleUUID();
        }
        else {
            uuid = UUID.fromString(roleIdStr);
        }
        HttpEntity<Role> httpRole = new HttpEntity<>(role);
        roleResponseEntity = restTemplate.exchange(URI_WITH_ID, HttpMethod.DELETE, httpRole, Role.class, uuid);

    }

    @And("role read should retrieve empty record")
    public void roleReadShouldRetrieveEmptyRecord() {
        clientRequestGETRoleRoleid(scenarioContext.getRoleUUID().toString());
        Assertions.assertThat(roleResponseEntity.getBody()).isEqualTo(new Role());

    }

    @When("role permission client request PUT \\/role\\/roleid\\/permission")
    public void rolePermissionClientRequestPUTRoleRoleidPermission() {
        Permission permission = Permission.builder().name(permissionToAdd).isEnabled(true).build();
        UUID roleId = scenarioContext.getRoleUUID();
        HttpEntity<Permission> httpPermission = new HttpEntity<>(permission);
        voidResponseEntity = restTemplate.exchange(URI_WITH_PERMISSION, HttpMethod.PUT, httpPermission, Void.class, roleId);
    }

    @Then("role permission endpoint should respond with http status code {int}")
    public void rolePermissionEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(voidResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("a new permission {string}")
    public void aNewPermissionAPP_USER_PERMISSION(String permissionName) {
        permissionToAdd = permissionName;
    }

    @When("role permission client request DELETE \\/role\\/roleId\\/permission\\/permissionid")
    public void rolePermissionClientRequestDELETERoleRoleIdPermissionPermissionid() {
        UUID roleId = scenarioContext.getRoleUUID();
        UUID permissionId = scenarioContext.getPermissionUUID();
        voidResponseEntity = restTemplate.exchange(URI_WITH_PERMISSION_ID, HttpMethod.DELETE,
                null, Void.class, roleId, permissionId);
    }
}
