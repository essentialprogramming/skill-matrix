Feature: An user can create a profile

  Scenario: Admin creates user account
    Given  Admin is logged in and create user endpoint exists
    When  Admin sends a valid create user payload
    Then  Response status code should be 201
    And  Create user response should be valid


  Scenario: User login with valid
    Given User login endpoint exists
    When The user sends valid credentials
    Then Response status should be 200
    And Extracted token should not be null

  Scenario: User login with invalid password
    Given User login endpoint exists
    When The user sends invalid credentials
    Then Response status should be 500
    And Extracted token should be null and the response message "The password you entered is wrong. Please Try again."

  Scenario: An user can create a profile
    Given User is logged in
    When User sends profile data
    Then Response status code should be 201
    And Create profile response should be valid