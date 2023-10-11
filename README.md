# Spring 5 REST API whiteapp

Cette whiteapp à pour but de montrer comment créer une API Rest avec springboot 2 en séparant au maximum le technique du fonctionnel.

[![codecov.io](https://codecov.io/gh/nduheron/spring-rest-api/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/github/nduheron/spring-rest-api?branch=master)

## Fonctionnalités

- Authentification [JWT](https://jwt.io/introduction/) avec [spring-security](https://docs.spring.io/spring-security/site/docs/5.0.0.RELEASE/reference/htmlsingle/)
- Internationalisation
- JSR-303 Bean Validation API
- BDD avec [Cucumber](https://cucumber.io/)
- Tests unitaire avec [Mockito](http://site.mockito.org/)
- Documentation avec [Swagger](https://swagger.io/)
- Monitoring avec Actuator, Prometheus et Grafana
- Mapping entité->DTO avec [Mapstruct](http://mapstruct.org/)
- Versionning Base de données avec [Flyway](https://flywaydb.org/)

## Structure du projet

* `fr.nduheron.poc.springrestapi.config` : Configuration de l'application
* `fr.nduheron.poc.springrestapi.security` : Lien entre spring-security et le métier lié aux utilisateurs
* `fr.nduheron.poc.springrestapi.tools.rest.errors` : Transforme les exceptions en réponse REST
* `fr.nduheron.poc.springrestapi.tools.rest.log` : Log toutes les requêtes et réponses des appels REST
* `fr.nduheron.poc.springrestapi.tools.rest.security` : Ajout de JWT à spring-security (filtre HTTP permettant de
  décoder le jeton JWT, configuration des URLs protégées, ajout des réponses 401 et 403 à la documentation Swagger quan
  nécessaire)
* `fr.nduheron.poc.springrestapi.tools.openapi` : Gestion automatique de la documentation swagger pour les erreurs
* `fr.nduheron.poc.springrestapi.user` : Contient toutes les classes métiers (Controller, DAO, POJO métier...)

## Gestion des exceptions

###### Contrôles de surface

Les contrôles de surfaces sont effectués grâce à l'api de validation de java (JSR-303). Les implémentations de
l'interface [BadRequestExceptionMapper](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/errors/mapper/BadRequestExceptionMapper.java)
permettent de transformer les erreurs en HTTP 400 avec le
modèle [AttributeErrorDto](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/errors/dto/AttributeErrorDto.java)

###### Erreurs métiers

Ces exception sont générées quand la méthode n'a pas pu rendre son fonctionnel nominal. L'exception ne signifie pas un
cas d'erreur mais un cas exceptionnel devant faire l'objet d'un traitement spécifique. Dans notre API Rest, ces
exceptions héritent de la
classe [FunctionalException](src/main/java/fr/nduheron/poc/springrestapi/tools/exceptions/FunctionalException.java) et
retournent un code HTTP 409 avec le
modèle [FunctionalErrorDto](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/errors/dto/FunctionalErrorDto.java).

###### Erreurs techniques

Il s'agit des exceptions irrécupérables fonctionnelement. Elles sont utilisées quand le client ne peut pas réagir. Dans
notre API Rest, ces exceptions retournent un code HTTP 500 sans aucun modèle afin de ne pas exposer trop d'informations
techniques au client.

###### Stratégie de log

Toutes les exceptions sont loguées automatiquement via le translateur
d'exception [RestExceptionTranslator](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/errors/RestExceptionTranslator.java).
Les erreures clientes (40x) sont loggués en WARN avec seulement le message de l'exception. Les exception techniques (
50x) sont logguées en ERROR avec toute la stacktrace. Il est donc important quand on transorme une exception en
Technical exception de propager la cause.

## Documentation

Les API REST étant consommés par des applications tiers, elles doivent avoir une documentation exhaustive et sans
ambiguité. Open api 3 est un standard utilisé pour produire la documentation en ligne des services REST et mettre à
disposition une interface web de test. [springdoc-openapi](https://springdoc.org/) permet de générer automatiquement une
API Spring au format openapi.

Un ensemble de ["customizers"](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/openapi/customizers) on été créés
afin de documenter automatiquement les erreurs:

* la 400 apparait automatiqement si un ```@RequestBody``` est présent dans les paramètres de la requête
* la 401 apparait automatiquement si un ```SecurityRequirement``` est présent sur l'opération
* la 403 apparait automatiquement si un ```SecurityRequirement``` est présent sur l'opération et que la resource possède
  une des annotations suivantes: ```@PreAuthorize```, ```@PostAuthorize```
* la 404 apparait automatiqement si un ```@PathParam``` est présent dans l'url de la resource
* la 500 apparait automatiquement sur toutes les resources

La factory [OpenAPIFactory](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/openapi/OpenAPIFactory.java) permet
de simplifier la création d'une api :

* version alimentée automatiquement via la version du pom
* Ajout des modèles d'erreurs

Après avoir lancé l'application la documentation est disponible à l'url http://localhost/docs

Exemple de configuration :

```java
    @Bean
public OpenAPIFactory userApi(Optional<BuildProperties> buildProperties,OpenApiProperties properties){
        OpenAPIFactory factory=new OpenAPIFactory(buildProperties.orElse(null),properties);
        factory.addSecurityScheme(OAUTH_PASSWORD_FLOW,
        new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT"));
        return factory;
        }
```

```yml
springdoc:
  default-consumes-media-type: application/json
  default-produces-media-type: application/json
  override-with-generic-response: false
  remove-broken-reference-definitions: false
  api-docs:
    title: User API
  swagger-ui:
    path: /docs
```

### Cache HTTP

Il est possible d'ajouter
un [Etag](https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers) à la réponse
HTTP en ajoutant l'annotation ```@Etag``` sur la méthode du controller. Dans ce cas, si le etag de la réponse correspond
au etag de la requête alors un code retour HTTP 304 (Not Modified) est retourné.

Il est également possible de configurer la durée maximale durant laquelle le client peut garder la réponse en cache (en
secondes): ```@Etag(maxAge=30)```. La même chose est possible en allant chercher la valeur dans le fichier de
configuration ```@Etag(maxAge="${accounts.me.maxAge}")```. Avec cette configuration le header HTTP Cache-Control est
retournée à la place du header ETag.

### Monitoring

L'aspect [TimedAspect](src/main/java/fr/nduheron/poc/springrestapi/tools/monitoring/TimedAspect.java) permet d'ajouter à
actuator le monitoring des couches: Repositories et Services.

La couche HTTP est monitorée via la configuration actuator:

```yml
management:
  metrics:
    distribution:
      percentiles:
        http:
          server:
            requests: 0.95,0.99
```

Après avoir lancé l'application, le monitoring est accessible depuis Grafana à l'url http://localhost/monitoring

## Logs

La classe [ApiLoggingFilter](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/log/ApiLoggingFilter.java) permet de
logguer toutes les requêtes HTTP suivant les règles suivantes:

* Les entêtes (de la requête et de la réponse) et le body de la réponse sont logguer seulement en DEBUG.
* Le body de la requête est logguer seulement en DEBUG ou bien si l'api est en erreur.

Le niveau de log dépend du code retour HTTP:

* 20x = INFO
* 40x = WARN
* 50x = ERROR

#### Configuration

* `log.filter.path` : Active le logguer. La valeur attendue est
  un [pattern](https://confluence.atlassian.com/fisheye/pattern-matching-guide-960155410.html) des urls à logguer
* `log.filter.exclude-paths` : Liste des patterns des URLs à exclure des logs
* `log.filter.obfuscate-params` : Permet d'obfusquer certains paramètres du corps de la requête et de la réponse. Par
  défaut, l'attribut _password_ est obfusqué.
* `log.filter.obfuscate-header` : Permet d'obfusquer certains headers de la requête et de la réponse. Par défaut,
  l'attribut _Authorization_ est obfusqué.
* `log.filter.body-enabled` : Permet d'activer les logs des body (actif par défaut).

Après avoir lancé l'application, les logs sont accessibles depuis Kibana à l'url http://localhost/logs

## Authentification [JWT](https://jwt.io/introduction/) avec [spring-security](https://docs.spring.io/spring-security/site/docs/5.0.0.RELEASE/reference/htmlsingle/)

La
classe [JwtTokenAuthenticationProcessingFilter](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/security/jwt/JwtTokenAuthenticationProcessingFilter.java)
et un filtre permettant de valider un bearer token dans le header Authorization. Pour le configuer il faut définir :

* Les urls sur lesquels appliquer le filtre

```yaml
security:
  config:
    includes:
      - ant-pattern: /**
    excludes:
      - ant-pattern: /actuator/**
```

* Les règles de validation du JWT

```yaml
security:
  jwt:
    issuer: spring-rest-api
    public-key: file:/public.crt
```

* Un converter
  implémentant [JwtAuthenticationTokenConverter](src/main/java/fr/nduheron/poc/springrestapi/tools/rest/security/jwt/JwtAuthenticationTokenConverter.java)
  qui à pour but de transformer les claims du JWT en contexte utilisateur.

## Installation

#### Prérequis

- Java 11
- Maven 3
- Docker

### Commandes

* Génération de l'application

```bash
mvn clean verify
```

* Créer le fichier d'environnement _./docker/conf/.env.prod_

```properties
SPRING_REST_DB_PASSWORD=changeme
DOMAIN=localhost
SPRING_REST_GRAFANA_ROOT_PASSWORD=changeme
ELASTIC_PASSWORD=changeme
KIBANA_PASSWORD=changeme
KIBANA_ADMIN_PASSWORD=changeme
KIBANA_GUEST_PASSWORD=changeme
CERT_COUNTRY_CODE=FR
CERT_STATE=France
CERT_LOCALITY=Caen
CERT_ORGANIZATION=perso
CERT_ORGANIZATIONAL_UNIT=
```

* Lancement de l'application
```bash
docker compose --env-file ./docker/config/.env.prod up --build --force-recreate
```

* Lancement de l'application en local

```bash
docker compose -f docker-compose-local up
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## TODO

### Grafana

* Ajouter/Créer les graphiques pour les repositories dans le board _Api Monitoring_

### NGINX

* add https