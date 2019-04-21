# Spring 5 REST API whiteapp

Cette whiteapp à pour but de montrer comment créer une API Rest avec springboot 2 en séparant au maximum le technique du fonctionnel.

[![Build Status](https://travis-ci.org/nduheron/spring-rest-api.svg?branch=master)](https://travis-ci.org/nduheron/spring-rest-api) 
[![codecov.io](https://codecov.io/gh/nduheron/spring-rest-api/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/github/nduheron/spring-rest-api?branch=master)

## Documentation

- [Api](./documentation/api.adoc)
- [Features](./documentation/features.adoc)

## Fonctionnalités

- Authentification [JWT](https://jwt.io/introduction/) avec [spring-security](https://docs.spring.io/spring-security/site/docs/5.0.0.RELEASE/reference/htmlsingle/)
- Internationalisation
- JSR-303 Bean Validation API
- BDD avec [Cucumber](https://cucumber.io/)
- Tests unitaire avec [Mockito](http://site.mockito.org/)
- Documentation avec [Swagger](https://swagger.io/)
- Monitoring avec Actuator
- La gestion des erreurs
- Mapping entité->DTO avec [Mapstruct](http://mapstruct.org/)
- Versionning Base de données avec [Flyway](https://flywaydb.org/)
- Versionning des APIs

## Structure du projet

* `fr.nduheron.poc.springrestapi.config` : Configuration de l'application
* `fr.nduheron.poc.springrestapi.security` : Lien entre spring-security et le métier lié aux utilisateurs
* `fr.nduheron.poc.springrestapi.tools.actuator` : Addons pour actuator (Nouveau endpoint pour la  gestion des caches, ajout d'actuator à la documentation Swagger, sécurisisation des endpoints)
* `fr.nduheron.poc.springrestapi.tools.exception` : Transforme les exceptions en réponse REST
* `fr.nduheron.poc.springrestapi.tools.log` : Log toutes les requêtes et réponses des appels REST
* `fr.nduheron.poc.springrestapi.tools.security` : Ajout de JWT à spring-security (filtre HTTP permettant de décoder le jeton JWT, configuration des URLs protégées, ajout des réponses 401 et 403 à la documentation Swagger quan nécessaire)
* `fr.nduheron.poc.springrestapi.tools.swagger` : Gestion automatique de la documentation swagger pour les erreurs
* `fr.nduheron.poc.springrestapi.user` : Contient toutes les classes métiers (Controller, DAO, POJO métier...)

## Installation

#### Prérequis

- Java 8
- Maven 3
- Docker

### Commandes

```bash
mvn docker:build docker:start
mvn package
java -jar spring-rest-api-0.0.1-SNAPSHOT.jar
```




