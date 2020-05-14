Feature: Roles

  Ensures that roles are added, modified and deleted

  # POST /role : success scenarios
  Scenario Outline: Create a role with valid data
    Given role for add: <roleName>
    When client request POST /role
    Then role endpoint should respond with http status code 200
    And the role response should contain valid UUID

    Examples: Add role with valid data input
      | roleName      |
      | ADMIN_ROLE    |
      | APP_USER_ROLE |

  # POST /role : failure scenarios
  Scenario Outline: Create a role with invalid data
    Given role for add: <roleName>
    When  client request POST /role
    Then  role endpoint should respond with http status code <statusCode>

    Examples: Adding a role using invalid data input
      | roleName | statusCode |
      |          | 400        |

    Examples: Adding a role that already exists!
      | roleName      | statusCode |
      | APP_USER_ROLE | 409        |

  # GET /role/uuid - Valid
  Scenario: Read a role with valid Id
    Given system knows about the role: ADMIN_ROLE
    When  client request GET /role/roleid
    Then  role endpoint should respond with http status code 200
    And the role response should be JSON:
      """
      { "name": "ADMIN_ROLE" }
      """

  # GET /role/uuid - Invalid Valid
  Scenario: Read a role with invalid id
    Given invalid role id 35698999-2205-4682-b1eb-c1b4210f363b
    When  client request GET /role/35698999-2205-4682-b1eb-c1b4210f363b
    Then  role endpoint should respond with http status code 404

  # GET /role/ - Find All
  Scenario: Find all roles
    Given System knows about GOOGLE_ADMIN_ROLE, ADMIN_ROLE, APP_USER_ROLE
    When  client request GET /role/
    Then  findAll role endpoint should respond with http status code 200
    And findAll role size is 3 and response should be JSON:
      """
      [{"id":null,"name": "GOOGLE_ADMIN_ROLE", "users": [], "permissions":[]},
      {"id":null,"name": "ADMIN_ROLE", "users": [], "permissions":[]},
      {"id":null,"name": "APP_USER_ROLE", "users": [], "permissions":[]}]
      """

  # PUT /role/uuid - Valid
  Scenario: Update role success
    Given system knows about the role: ADMIN_ROLE
    When  client request PUT /role/roleId
    And update role with ADMIN_ROLE_123
    Then  role endpoint should respond with http status code 200
    And role read should retrieve the updates
    And the role response should be JSON:
      """
      { "name": "ADMIN_ROLE_123" }
      """

  # PUT /role/uuid - Role already exists error
  Scenario: Update role with role name that already exists
    Given system knows about the role: ADMIN_ROLE_123
    When  client request PUT /role/roleId
    And update role with APP_USER_ROLE
    Then  role endpoint should respond with http status code 409

  # PUT /role/uuid - Role not found error
  Scenario: Update role using invalid id
    Given invalid role id ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    When  client request PUT /role/ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    And update role with APP_NOACCESS_ROLE
    Then  role endpoint should respond with http status code 404

  # Permissions tests on a role
  # PUT /role/roleid/permission
  Scenario: Assign a permission to a role
    Given system knows about the role: ADMIN_ROLE_123
    And a new permission 'APP_USER_PERMISSION'
    When  role permission client request PUT /role/roleid/permission
    Then  role permission endpoint should respond with http status code 200
    And role read should retrieve the updates
    And the role response should be JSON:
      """
      {"name":"ADMIN_ROLE_123","permissions":[{"name":"APP_USER_PERMISSION","enabled":true}]}
      """

  Scenario: Delete Permission with valid Id
    Given system knows about the role: ADMIN_ROLE_123 and the Permission APP_USER_PERMISSION
    When  role permission client request DELETE /role/roleId/permission/permissionid
    Then  role permission endpoint should respond with http status code 200

  # DELETE: test delete role at the end.
  # DELETE /role/uuid - Valid
  Scenario: Delete role with valid Id
    Given system knows about the role: ADMIN_ROLE_123
    When  role client request DELETE /role/roleId
    Then  role endpoint should respond with http status code 200
    And role read should retrieve empty record

  # DELETE /role/uuid - Invalid Id
  Scenario: Delete role with invalid id
    Given invalid role id ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    When  role client request DELETE /role/ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    Then  role endpoint should respond with http status code 404

