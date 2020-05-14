package com.sree.rampup.users.integrationtest.context;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
//@Scope("cucumber-glue")
public class ScenarioContext {
    private UUID userUUID;
    private UUID roleUUID;
    private UUID userRoleUUID;
    private UUID permissionUUID;
}