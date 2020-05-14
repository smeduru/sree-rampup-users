package com.sree.rampup.users.integrationtest;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features", plugin = {"pretty", "html:build/cucumber", "json:build/cucumber.json"})
public class CucumberIntegration {
}
