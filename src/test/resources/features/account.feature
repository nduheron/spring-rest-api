Feature: account operations

  @dbunit
  Scenario: Récupérer mon profil utilisateur sans être  connecté
    Given users datasets
    When I get account
    Then I get a UNAUTHORIZED response

  @dbunit
  Scenario: Récupérer mon profil utilisateur
    Given users datasets
    And I login with batman
    When I get account
    Then I get a OK response
    And my name is Bruce Wayne
    And I am SYSTEM user

  @dbunit
  Scenario: Modifier son mot de passe sans être connecté
    Given users datasets
    When I change 12345 password with 123456
    Then I get a UNAUTHORIZED response

  @dbunit
  Scenario: Modifier son mot de passe
    Given users datasets
    And I login with batman
    When I change 12345 password with 123456
    Then I get a NO_CONTENT response

  @dbunit
  Scenario: Modifier son mot de passe avec une erreur d'authentification'
    Given users datasets
    And I login with batman
    When I change 123456 password with 123456
    Then I get a FORBIDDEN response
