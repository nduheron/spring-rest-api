Feature: authentification operations

  @dbunit
  Scenario Outline: Login
    Given users datasets
    When I login with username <username> and password <password>
    Then I get a <statusCode> response

    Examples: 
      | username      | password | statusCode   | description                   |
      | x             | x        | BAD_REQUEST  | Paramètres incorrects         |
      | blackpanther  |    12345 | UNAUTHORIZED | l'utilisateur n'existe pas    |
      | humantorch    |    12345 | UNAUTHORIZED | l'utilisateur n'est pas actif |
      | invisiblegirl |   123456 | UNAUTHORIZED | Mot de passe invalide         |
      | invisiblegirl |    12345 | OK           |                               |
