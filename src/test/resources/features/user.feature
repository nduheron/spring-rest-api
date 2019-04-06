Feature: users operations

@dbunit
Scenario: l'administrateur peut rechercher des utilisateurs
	Given users datasets
	And I login with spiderman
	When I get all users
	Then I get a OK response
	And 4 users found

@dbunit
Scenario: l'administrateur système peut rechercher des utilisateurs
	Given users datasets
	And I login with batman
	When I get all users
	Then I get a OK response
	And 4 users found

  @dbunit
  Scenario: l'administrateur système peut exporter des utilisateurs au format CSV
    Given users datasets
    And I login with batman
    When I get all users in csv
    Then I get a OK response
    And 4 users found in csv

@dbunit
Scenario: Un utilisateur lambda n'a pas le droit de rechercher des utilisateurs
	Given users datasets
	And I login with invisiblegirl
	When I get all users
	Then I get a FORBIDDEN response

@dbunit
Scenario: l'administrateur système peut rechercher un utilisateur
	Given users datasets
	And I login with batman
	When I search user invisiblegirl
	Then I get a OK response
	And the user has name Jane Storm

@dbunit
Scenario: l'administrateur peut rechercher un utilisateur
	Given users datasets
	And I login with spiderman
	When I search user invisiblegirl
	Then I get a OK response
	And the user has name Jane Storm

@dbunit
Scenario: Un utilisateur lambda n'a pas le droit de rechercher un utilisateur
	Given users datasets
	And I login with invisiblegirl
	When I search user batman
	Then I get a FORBIDDEN response

@dbunit
Scenario: Rechercher un utilisateur qui n'existe pas
	Given users datasets
	And I login with batman
	When I search user blackpanther
	Then I get a NOT_FOUND response

@dbunit
Scenario: l'administrateur système n'a pas le droit de supprimer un utilisateur
	Given users datasets
	And I login with batman
	When I delete user invisiblegirl
	Then I get a FORBIDDEN response

@dbunit
Scenario: l'administrateur peut supprimer un utilisateur
	Given users datasets
	And I login with spiderman
	When I delete user invisiblegirl
	Then I get a NO_CONTENT response
	And the user invisiblegirl is deleted

@dbunit
Scenario: Un utilisateur lambda n'a pas le droit de supprimer un utilisateur
	Given users datasets
	And I login with invisiblegirl
	When I delete user batman
	Then I get a FORBIDDEN response

@dbunit
Scenario: La suppression est idempotente
	Given users datasets
	And I login with spiderman
	When I delete user invisiblegirl
	And I delete user invisiblegirl
	Then I get a NO_CONTENT response

@dbunit
Scenario: l'administrateur système n'a pas le droit de créer un utilisateur
	Given users datasets
	And I login with batman
	When I create ironman user
	Then I get a FORBIDDEN response

@dbunit
Scenario: l'administrateur peut créer un utilisateur
	Given users datasets
	And I login with spiderman
	When I create ironman user
	Then I get a CREATED response
	And the user ironman is created
	And email password is sent

@dbunit
Scenario: Un utilisateur lambda n'a pas le droit de créer un utilisateur
	Given users datasets
	And I login with invisiblegirl
	When I create ironman user
	Then I get a FORBIDDEN response

@dbunit
Scenario: Créer un utilisateur sans remplir les champs obligatoires
	Given users datasets
	And I login with spiderman
	When I create empty user
	And I get a BAD_REQUEST response
	And I get 5 parameters in error

@dbunit
Scenario: Créer un utilisateur déjà existant
	Given users datasets
	And I login with spiderman
	When I create invisiblegirl user
	Then I get a BAD_REQUEST response
	And I get a ALREADY_EXIST error

@dbunit
Scenario: l'administrateur système n'a pas le droit de modifier un utilisateur
	Given users datasets
	And I login with batman
	When I update batman with superman data
	Then I get a FORBIDDEN response

@dbunit
Scenario: l'administrateur peut modifier un utilisateur
	Given users datasets
	And I login with spiderman
	When I update batman with superman data
	Then I get a NO_CONTENT response
	And the user batman has name Clark Kent

@dbunit
Scenario: Un utilisateur lambda n'a pas le droit de modifier un utilisateur
	Given users datasets
	And I login with invisiblegirl
	When I update batman with superman data
	Then I get a FORBIDDEN response

@dbunit
Scenario: Modifier un utilisateur sans remplir les champs obligatoires
	Given users datasets
	And I login with spiderman
	When I update batman with empty data
	And I get a BAD_REQUEST response
	And I get 4 parameters in error

@dbunit
Scenario: Modifier un utilisateur qui n'existe pas
	Given users datasets
	And I login with spiderman
	When I update blackpanther with superman data
	And I get a NOT_FOUND response

  @dbunit
  Scenario: l'administrateur système n'a pas le droit de réinitialiser le mot de passe d'un utilisateur
	Given users datasets
	And I login with batman
	When I reinit password to invisiblegirl
	Then I get a FORBIDDEN response

@dbunit
Scenario: l'administrateur système peut réinitialiser son mot de passe
	Given users datasets
	And I login with batman
	When I reinit password to batman
	Then I get a NO_CONTENT response
	And the password to batman has changed
	And email reinit password is sent

@dbunit
Scenario: l'administrateur a le droit de réinitialiser le mot de passe de n'importe quel utilisateur
	Given users datasets
	And I login with spiderman
	When I reinit password to invisiblegirl
	Then I get a NO_CONTENT response
	And the password to invisiblegirl has changed
	And email reinit password is sent

@dbunit
Scenario: un utilisateur lambda n'a pas le droit de réinitialiser le mot de passe d'un autre utilisateur
	Given users datasets
	And I login with invisiblegirl
	When I reinit password to spiderman
	Then I get a FORBIDDEN response

@dbunit
Scenario: un utilisateur lambda peut réinitialiser son mot de passe
	Given users datasets
	And I login with invisiblegirl
	When I reinit password to invisiblegirl
	Then I get a NO_CONTENT response
	And the password to invisiblegirl has changed
	And email reinit password is sent

@dbunit
Scenario: Réinitialiser le mot de passe d'un utilisateur qui n'existe pas
	Given users datasets
	And I login with spiderman
	When I reinit password to superman
	Then I get a NOT_FOUND response
