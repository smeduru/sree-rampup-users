package com.sree.rampup.users.integrationtest.steps;

import com.sree.rampup.users.integrationtest.context.ScenarioContext;
import com.sree.rampup.users.model.Permission;
import com.fasterxml.jackson.core.type.TypeReference;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sree.rampup.users.util.JsonMarshaller.parseJSON;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@RequiredArgsConstructor
public class PermissionSteps {
    public static final String URI ="/v1/permission";
    public static final String URI_WITH_ID = URI + "/{uuid}";

    private ResponseEntity<Permission> permissionResponseEntity;
    private ResponseEntity<String> stringResponseEntity;
    private ResponseEntity<Void> voidResponseEntity;

    private final ScenarioContext scenarioContext;

    private final TestRestTemplate restTemplate;

    @Given("System knows about READONLY_PERMISSION, REPORTS_PERMISSION, POLICY_PERMISSION")
    public void systemKnowsAboutREADONLY_PERMISSIONREPORTS_PERMISSIONPOLICY_PERMISSION() {
        //Do nothing
    }

    @When("permission client request GET \\/permission\\/")
    public void permissionClientRequestGETRole() {
        stringResponseEntity = restTemplate.getForEntity(URI + "/", String.class);
    }

    @Then("findAll permission endpoint should respond with http status code {int}")
    public void findallPermissionEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(stringResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("findAll permission size is (\\d+) and response should be JSON:$")
    public void findallPermissionSizeIsAndResponseShouldBeJSON(int size, String json) {
        TypeReference<List<Permission>> typeReference = new TypeReference<List<Permission>>() { };
        try {
            List<Permission> returnedList = parseJSON(stringResponseEntity.getBody(), typeReference);
            List<Permission> expectedList = parseJSON(json, typeReference);
            Assertions.assertThat(returnedList.size()).isEqualTo(size);
            Permission permission1 = returnedList.get(0);
            Permission permission2 = returnedList.get(1);
            Permission permission3 = returnedList.get(2);

            Assertions.assertThat(expectedList).extracting("name", "enabled")
                    .contains(tuple(permission1.getName(), permission1.isEnabled()),
                        tuple(permission2.getName(), permission2.isEnabled()),
                        tuple(permission3.getName(), permission3.isEnabled()));
        }
        catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Exception during JSON Parsing: \n%s \n%s", stringResponseEntity.getBody(), json);
        }
    }

    @Given("system knows about the permission: READONLY_PERMISSION")
    public void systemKnowsAboutThePermissionREADONLY_PERMISSION() {
        // Do nothing
    }

    @When("client request GET \\/permission\\/(.+)$")
    public void clientRequestGETPermissionPermissionId(String permissionId) {
        permissionResponseEntity = restTemplate.getForEntity(URI_WITH_ID, Permission.class, permissionId);
    }

    @Then("permission endpoint should respond with http status code {int}")
    public void permissionEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(permissionResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the permission response should be JSON:$")
    public void thePermissionResponseShouldBeJSON(String json) throws Exception {
        Permission returnedPermission = permissionResponseEntity.getBody();
        Permission expectedPermission = parseJSON(json, Permission.class);
        Assertions.assertThat(returnedPermission).extracting("name", "enabled")
                .contains(expectedPermission.getName(), expectedPermission.isEnabled());
    }

    @And("is enabled")
    public void isEnabled() {
        //Do nothing
    }

    @When("permission client request PUT /permission/(.*)$")
    public void permissionClientRequestPUTPermissionCFAcAAFDfCC(String permissionIdStr) {
        UUID permissionId = UUID.fromString(permissionIdStr);
        Map<String, Boolean> map = new HashMap<>();
        map.put("enabled", false);
        HttpEntity<Map<String, Boolean>> httpEntity = new HttpEntity<>(map);
        voidResponseEntity = restTemplate.exchange(URI_WITH_ID, HttpMethod.PUT, httpEntity, Void.class, permissionId);
    }

    @Then("permission endpoint for update should respond with http status code {int}")
    public void permissionEndpointForUpdateShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(voidResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }
}
