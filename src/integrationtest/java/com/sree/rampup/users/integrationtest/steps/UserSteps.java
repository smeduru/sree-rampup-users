package com.sree.rampup.users.integrationtest.steps;

import com.sree.rampup.users.integrationtest.context.ScenarioContext;
import com.sree.rampup.users.model.Role;
import com.sree.rampup.users.model.User;
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
import static org.assertj.core.groups.Tuple.tuple;

@RequiredArgsConstructor
public class UserSteps {
    private static final String URI ="/v1/user";
    private static final String URI_WITH_ID ="/v1/user/{uuid}";
    private static final String URI_USER_ROLE_WITH_IDS = "/v1/user/{userid}/role/{roleid}";

    private ResponseEntity<User> userResponseEntity;
    private ResponseEntity<String> stringResponseEntity;
    private ResponseEntity<Void> voidResponseEntity;
    private ResponseEntity<Role> roleResponseEntity;

    private User user;
    private UUID invalidUUID;

    private final ScenarioContext scenarioContext;

    private final TestRestTemplate restTemplate;

    @Given("^user for add: (\\w*), (\\w*), (.*)$")
    public void userForAdd(String firstName, String lastName, String email) {
        user =  User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

    @When("client request POST /user")
    public void clientRequestPOSTUser() {
        userResponseEntity = restTemplate.postForEntity(URI, user, User.class);
    }

    @Then("endpoint should respond with http status code (\\d+)$")
    public void endpointShouldRespondWithHttpStatusCodeStatusCode(int statusCode) {
        Assertions.assertThat(userResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the response should contain valid UUID")
    public void theResponseShouldContainValidUUID() {
        Assertions.assertThat(userResponseEntity.getBody().getId()).isNotNull();
        if (scenarioContext.getUserUUID() == null) {
            scenarioContext.setUserUUID(userResponseEntity.getBody().getId());
        }
    }

    @Given("^system knows about the user: (\\w+), (\\w+), (.+)$")
    public void systemKnowAboutTheUser(String firstName, String lastName, String email) {
        Assertions.assertThat(scenarioContext.getUserUUID()).isNotNull();
    }

    @When("^client request GET \\/user\\/(.*)$")
    public void clientRequestGETUserUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) { // findAll
            stringResponseEntity = restTemplate.getForEntity(URI + "/", String.class);
        }
        else if ("userid".equals(uuidStr)) { // UUID interpolation
            UUID uuid = scenarioContext.getUserUUID();
            userResponseEntity = restTemplate.getForEntity(URI_WITH_ID, User.class, uuid);
        }
        else { // Actual UUID
            UUID uuidParam = UUID.fromString(uuidStr);
            userResponseEntity = restTemplate.getForEntity(URI_WITH_ID, User.class, uuidParam);
        }
    }

    @And("^the response should be JSON:$")
    public void theResponseShouldBeJSON(String json) {
        User responseUser = userResponseEntity.getBody();
        try {
            User expectedUser = JsonMarshaller.parseJSON(json, User.class);
            Assertions.assertThat(responseUser).isEqualToIgnoringGivenFields(expectedUser, "id", "roles");
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Given("^invalid user id.*$")
    public void invalidUserIdUnknownID() {
        // Do nothing.
    }

    @When("client request PUT /user/([^/]*)$")
    public void clientRequestPUTUserUuid(String uuid) {
        if (!"userid".equals(uuid)) { // Invalid scenario
            invalidUUID = UUID.fromString(uuid);
        }
    }

    @And("^update user with (\\w+), (\\w+), (.*)$")
    public void updateUserWith(String firstName, String lastName, String email) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        if (invalidUUID != null) {
            user.setId(invalidUUID);
        }
        else {
            user.setId(scenarioContext.getUserUUID());
        }

        HttpEntity<User> httpUser = new HttpEntity<>(user);
        userResponseEntity = restTemplate.exchange(URI_WITH_ID, HttpMethod.PUT, httpUser,
                User.class, user.getId());
    }

    @And("read should retrieve the updates")
    public void readShouldRetrieveTheUpdates() {
        clientRequestGETUserUuid(scenarioContext.getUserUUID().toString());
    }

    @When("client request DELETE /user/(.*)$")
    public void clientRequestDELETEUserUuid(String sUuid) {
        UUID uuid = null;
        if ("userid".equals(sUuid)) {
            uuid = scenarioContext.getUserUUID();
        }
        else {
            uuid = UUID.fromString(sUuid);
        }
        HttpEntity<User> httpUser = new HttpEntity<>(user);
        userResponseEntity = restTemplate.exchange(URI_WITH_ID, HttpMethod.DELETE, httpUser, User.class, uuid);
    }

    @And("read should retrieve empty record")
    public void readShouldRetrieveEmptyRecord() {
        clientRequestGETUserUuid(scenarioContext.getUserUUID().toString());
        Assertions.assertThat(userResponseEntity.getBody()).isEqualTo(new User());
    }

    @Given("System knows about Luana Abraham, Carl Chamber records")
    public void systemKnowsAboutLuanaAbrahamCarlChamberRecords() {
        // No need to do any thing.
    }

    @Then("^findAll endpoint should respond with http status code (\\d+)$")
    public void findAllEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(stringResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("findAll size is (\\d+) and response should be JSON:$")
    public void findAllResponseShouldBeJSON(int size, String json) {
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() { };
        try {
            List<User> expectedUserList = parseJSON(json, typeReference);
            List<User> returnedUserList = parseJSON(stringResponseEntity.getBody(), typeReference);
            Assertions.assertThat(returnedUserList.size()).isEqualTo(size);
            User user1 = returnedUserList.get(0);
            User user2 = returnedUserList.get(1);

            Assertions.assertThat(expectedUserList).extracting("firstName", "lastName", "email")
                    .contains(tuple(user1.getFirstName(), user1.getLastName(), user1.getEmail()),
                              tuple(user2.getFirstName(), user2.getLastName(), user2.getEmail()));
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Exception during JSON Parsing: \n%s \n%s", stringResponseEntity.getBody(), json);
        }
    }

    @When("user role client request PUT \\/user\\/userid\\/role\\/roleid")
    public void clientRequestPUTUserUseridRoleRoleid() {
        User user = userResponseEntity.getBody();

        UUID userId = user.getId();
        UUID roleId = scenarioContext.getUserRoleUUID(); //user.getRoles().iterator().next().getId();

        voidResponseEntity = restTemplate.exchange(URI_USER_ROLE_WITH_IDS, HttpMethod.PUT, null, Void.class, userId, roleId);
    }

    @Given("system knows about the user {string}, {string}, {string} and the role {string}")
    public void systemKnowsAboutTheUserAndTheRoleAPP_USER_ROLE(String firstName, String lastName, String email,
                                                               String roleName)  {
        clientRequestGETUserUuid(scenarioContext.getUserUUID().toString());
        User user = userResponseEntity.getBody();

        Role role =  Role.builder()
                .name(roleName)
                .build();

        ResponseEntity<Role> roleResponseEntity = restTemplate.postForEntity(RoleSteps.URI, role, Role.class);
        Role returnedRole = roleResponseEntity.getBody();
        scenarioContext.setUserRoleUUID(returnedRole.getId());
        user.addRole(returnedRole);
    }

    @Then("user role endpoint should respond with http status code {int}")
    public void userRoleEndpointShouldRespondWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(voidResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @Given("system knows about the user {string}, {string}, {string} and the assigned role {string}")
    public void systemKnowsAboutTheUserAndTheAssignedRoleAPP_USER_ROLE(String firstName, String lastName, String email, String rolename) {
        // Do nothing.
    }

    @When("user role client request DELETE \\/user\\/userid\\/role\\/roleid")
    public void userRoleClientRequestDELETEUserUseridRoleRoleid() {
        UUID userId = scenarioContext.getUserUUID();
        UUID roleId = scenarioContext.getUserRoleUUID();

        voidResponseEntity = restTemplate.exchange(URI_USER_ROLE_WITH_IDS, HttpMethod.DELETE, null, Void.class, userId, roleId);
    }

    @When("user role client request GET \\/user\\/userid\\/role\\/roleid")
    public void userRoleClientRequestGETUserUseridRoleRoleid() {
        UUID userId = scenarioContext.getUserUUID();
        UUID roleId = scenarioContext.getUserRoleUUID();

        roleResponseEntity = restTemplate.getForEntity(URI_USER_ROLE_WITH_IDS, Role.class, userId, roleId);
    }

    @Then("user role endpoint for role should with http status code {int}")
    public void userRoleEndpointForRoleShouldWithHttpStatusCode(int statusCode) {
        Assertions.assertThat(roleResponseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    }
}
