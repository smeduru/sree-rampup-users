Feature: Users

  Ensures that users are added, modified and deleted

  # POST /user : success scenarios
  Scenario Outline: Create an user with valid data
    Given user for add: <firstName>, <lastName>, <email>
    When  client request POST /user
    Then  endpoint should respond with http status code 200
    And the response should contain valid UUID

    Examples: Add user with valid data input
      | firstName | lastName | email                  |
      | Luana     | Abraham  | luana.abraham@test.com |
      | Carl      | Chambers | carl.chambers@test.com |

  # POST /user : failure scenarios
  Scenario Outline: Create an user with invalid data
    Given user for add: <firstName>, <lastName>, <email>
    When  client request POST /user
    Then  endpoint should respond with http status code <statusCode>

    Examples: Adding an user using invalid data input
      | firstName | lastName | email               | statusCode |
      | Steve     |          | steve.j@test.com    | 400        |
      | Kum       | Salmi    |                     | 400        |
      |           | Carver   | amy.carvor@test.com | 400        |

    Examples: Adding an user that already exists!
      | firstName | lastName | email                  | statusCode |
      | Luana     | Abraham  | luana.abraham@test.com | 409        |

  # GET /user/uuid - Valid
  Scenario: Read an user with valid Id
    Given system knows about the user: Luana, Abraham, luana.abraham@test.com
    When  client request GET /user/userid
    Then  endpoint should respond with http status code 200
    And the response should be JSON:
      """
      { "firstName": "Luana", "lastName": "Abraham", "email": "luana.abraham@test.com" }
      """

  # GET /user/uuid - Invalid id
  Scenario: Read an user with invalid id
    Given invalid user id ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    When  client request GET /user/ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    Then  endpoint should respond with http status code 404

  # GET /user/ - Find All
  Scenario: Find all users
    Given System knows about Luana Abraham, Carl Chamber records
    When  client request GET /user/
    Then  findAll endpoint should respond with http status code 200
    And findAll size is 3 and response should be JSON:
      """
      [{"id":null,"firstName":"Luana","lastName":"Abraham","email":"luana.abraham@test.com","roles":[]},
      {"id":null,"firstName":"Carl","lastName":"Chambers","email":"carl.chambers@test.com","roles":[]},
      {"id":null,"firstName":"Damon","lastName":"Alvarez","email":"damon.alvarez@test.com","roles":[]}
      ]
      """

  # PUT /user/uuid - Valid
  Scenario: Update user success
    Given system knows about the user: Luana, Abraham, luana.abraham@test.com
    When  client request PUT /user/userid
    And update user with Luana123, Abraham123, luana123.abraham123@test.com
    Then  endpoint should respond with http status code 200
    And read should retrieve the updates
    And the response should be JSON:
      """
      { "firstName": "Luana123", "lastName": "Abraham123", "email": "luana123.abraham123@test.com" }
      """

  # PUT /user/uuid - User already exists error
  Scenario: Update user with existing user
    Given system knows about the user: Luana123, Abraham123, luana123.abraham123@test.com
    When  client request PUT /user/userid
    And update user with Carl, Chambers, carl.chambers@test.com
    Then  endpoint should respond with http status code 409

  # PUT /user/uuid - User not found error
  Scenario: Update user using invalid id
    Given invalid user id ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    When  client request PUT /user/ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    And update user with Ethel, Harvey, ethel.harvey@test.com
    Then  endpoint should respond with http status code 404

  # GET /user/uuid - Invalid Valid
  Scenario: Delete user with invalid id
    Given invalid user id ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    When  client request DELETE /user/ab966346-54d4-4bb6-99c4-23c1aaf3fd7d
    Then  endpoint should respond with http status code 404

  # Roles.
  # PUT /usr/userid/role/roleid
  Scenario: Assign role to user
    Given system knows about the user 'Luana123', 'Abraham123', 'luana123.abraham123@test.com' and the role 'APP_SUPER_ADMIN_ROLE'
    When  user role client request PUT /user/userid/role/roleid
    Then  user role endpoint should respond with http status code 200

  Scenario: Find a role on an user
    Given system knows about the user 'Luana123', 'Abraham123', 'luana123.abraham123@test.com' and the assigned role 'APP_SUPER_ADMIN_ROLE'
    When  user role client request GET /user/userid/role/roleid
    Then  user role endpoint for role should with http status code 200

  # DELETE /user/userid/role/roleid
  Scenario: Remove a role from an user
    Given system knows about the user 'Luana123', 'Abraham123', 'luana123.abraham123@test.com' and the assigned role 'APP_SUPER_ADMIN_ROLE'
    When  user role client request DELETE /user/userid/role/roleid
    Then  user role endpoint should respond with http status code 200

  # Test DELETE at the end.
  # DELETE /user/uuid - Valid
  Scenario: Delete user with valid Id
    Given system knows about the user: Luana123, Abraham123, luana123.abraham123@test.com
    When  client request DELETE /user/userid
    Then  endpoint should respond with http status code 200
    And read should retrieve empty record

