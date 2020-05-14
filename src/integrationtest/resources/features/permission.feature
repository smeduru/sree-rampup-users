Feature: Permissions

    Ensures that roles are added, modified and deleted

    # GET /permission/ - Find All permissions
    Scenario: Find all permissions
        Given System knows about READONLY_PERMISSION, REPORTS_PERMISSION, POLICY_PERMISSION
        When  permission client request GET /permission/
        Then  findAll permission endpoint should respond with http status code 200
        And findAll permission size is 3 and response should be JSON:
            """
            [{"name": "READONLY_PERMISSION", "enabled": true },
            {"name": "REPORTS_PERMISSION", "enabled": true},
            {"name": "POLICY_PERMISSION", "enabled": true}]
            """

    # GET /permission/uuid - Valid
    Scenario: Read a Permission with valid Id
        Given system knows about the permission: READONLY_PERMISSION
        When  client request GET /permission/587c0573-6f70-44ac-a077-a4f2df2c53c5
        Then  permission endpoint should respond with http status code 200
        And the permission response should be JSON:
            """
            { "name": "READONLY_PERMISSION", "enabled": true }
            """
  # PUT /permission/uuid
  Scenario: Disable a permission
    Given system knows about the permission: READONLY_PERMISSION
    And is enabled
    When  permission client request PUT /permission/587c0573-6f70-44ac-a077-a4f2df2c53c5
    Then  permission endpoint for update should respond with http status code 200