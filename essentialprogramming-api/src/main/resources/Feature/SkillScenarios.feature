Feature: Skill POST

  Scenario: Add new category on /api/skill/add-category endpoint
    Given the add category endpoint and admin token exists
    When an admin will submit the category name as a query parameter
    Then the server will add that category to the database and return a JSON of it

  Scenario: Skill category already exists on /api/skill/add-category endpoint
    Given the add category endpoint and admin token exists
    When an admin will submit the category name as a query parameter
    Then the server will return 422 and category already exists

  Scenario: Add new skill on /api/skill/add endpoint
    Given the add skill endpoint and admin token exists
    When an admin will submit a skill name and category key as query parameters
    Then the server will create a new skill for each skill level for the given category

  Scenario: Skill already exists on /api/skill/add endpoint
    Given the add skill endpoint and admin token exists
    When an admin will submit a skill name and category key as query parameters
    Then the server will return 422 and skill already exists

  Scenario: Skill category not found on /api/skill/add endpoint
    Given the add skill endpoint and admin token exists
    When an admin will submit a skill name and category key as query parameters but the category does not exist
    Then the server will return 404 and category not found

  Scenario: Associate a skill to a profile on /api/profile/add/profile/skill endpoint
    Given associate a skill to a profile endpoint exists
    When a user will submit a skill key as a query parameter
    Then the server will associate that skill to the user's profile, based on the email extracted from the JWT

  Scenario: Profile not found on /api/profile/add/profile/skill endpoint
    Given associate a skill to a profile endpoint exists but no profile is created
    When a user will submit a skill key as a query parameter
    Then the server will return 404 and profile not found

  Scenario: Skill not found on /api/profile/add/profile/skill endpoint
    Given associate a skill to a profile endpoint exists
    When a user will submit a wrong skill key as a query parameter
    Then the server will return 404 and skill not found