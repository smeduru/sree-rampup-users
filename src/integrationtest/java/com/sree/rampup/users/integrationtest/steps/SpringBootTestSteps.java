package com.sree.rampup.users.integrationtest.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

@RequiredArgsConstructor
@ActiveProfiles("integrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootTestSteps {
    private final TestRestTemplate testRestTemplate;
//    private final RestTemplate restTemplate;

    @Given("SpringBootTest annotation is applied")
    public void springboottest_annotation_is_applied() {
    }

    @Then("required components are available for autowiring")
    public void required_components_are_available_for_autowiring() {
        assertNotNull(testRestTemplate);
//        assertNotNull(restTemplate);
    }
}
