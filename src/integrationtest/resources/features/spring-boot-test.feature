Feature: Spring Boot Test

  This feature acts as a test harness for setting up Spring Boot Test environment that will be used for all the other tests

  Scenario: Spring Boot Test environment is setup
    Given SpringBootTest annotation is applied
    Then required components are available for autowiring