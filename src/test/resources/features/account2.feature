Feature: account operations version 2

@dbunit
Scenario: Modifier son mot de passe sans être connecté
	Given users datasets
	And version 2
	When I change 12345 password with 123456
	Then I get a UNAUTHORIZED response

@dbunit
Scenario: Modifier son mot de passe
	Given users datasets
	And version 2
	And I login with batman
	When I change 12345 password with 123456
	Then I get a NO_CONTENT response

@dbunit
Scenario: Modifier son mot de passe avec une erreur d'authentification'
	Given users datasets
	And version 2
	And I login with batman
	When I change 123456 password with 123456
	Then I get a FORBIDDEN response

