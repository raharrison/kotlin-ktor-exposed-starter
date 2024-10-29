[![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/ktor-3.0.0-blue.svg)](https://github.com/ktorio/ktor)
[![Build](https://github.com/raharrison/kotlin-ktor-exposed-starter/workflows/Build/badge.svg)](https://github.com/raharrison/kotlin-ktor-exposed-starter/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/raharrison/kotlin-ktor-exposed-starter/branch/master/graph/badge.svg?token=v2k9oObm0C)](https://codecov.io/gh/raharrison/kotlin-ktor-exposed-starter)

## Starter project to create a simple RESTful web service in Kotlin

**Updated for Kotlin 2.0.21 and Ktor 3.0.0**

Companion article: <https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html>

## Getting Started

1. Clone the repo.
2. In the root directory execute `./gradlew run`
3. By default, the server will start on port `8080`. See below [Routes](#routes) section for more information.

### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON serialization/deserialization
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Flyway](https://flywaydb.org/) - Database migrations
 - [JUnit 5](https://junit.org/junit5/), [AssertJ](http://joel-costigliola.github.io/assertj/)
   and [Rest Assured](http://rest-assured.io/) for testing
 - [Kover](https://github.com/Kotlin/kotlinx-kover) for code coverage, publishing
   to [Codecov](https://about.codecov.io/) through GitHub Actions
 
The starter project creates a new in-memory H2 database with one table for `Widget` instances.

As ktor is async and based on coroutines, standard blocking JDBC may cause performance issues when used
directly on the main thread pool (as threads must be reused for other requests). Therefore, another dedicated thread
pool is created for all database queries, alongside connection pooling with HikariCP. 

### Routes:

`GET /widgets` --> get all widgets in the database

`GET /widgets/{id}` --> get one widget instance by id (integer)

`POST /widgets` --> add a new widget to the database by providing a JSON object (converted to a NewWidget instance). e.g - 

```json
{
    "name": "new widget",
    "quantity": 64
}
```

returns

```json
{
    "id": 3,
    "name": "new widget",
    "quantity": 64,
    "dateUpdated": 1519926898
}
```  
    
`PUT /widgets` --> update an existing widgets name or quantity. Pass in the id in the JSON request to determine which record to update

`DELETE /widgets/{id}` --> delete the widget with the specified id

### Notifications (WebSocket)

All updates (creates, updates and deletes) to `Widget` instances are served as notifications through a WebSocket endpoint:

`WS /updates` --> returns `Notification` instances containing the change type, id and entity (if applicable) e.g:

```json
{ 
    "type": "CREATE", 
    "id": 12, 
    "entity": { 
      "id": 12, 
      "name": "widget1", 
      "quantity": 5, 
      "dateUpdated": 1533583858169 
    }
}
```

The websocket listener will also log out any text messages send by the client. Refer to [this blog post](https://ryanharrison.co.uk/2018/08/19/testing-websockets.html) for some useful tools to test the websocket behaviour.

### Testing

The sample Widget service and corresponding endpoints are also tested with 100% coverage. Upon startup of the main JUnit suite (via the `test` source folder), the server is started ready for testing and is torn down after all tests are run.

- Unit testing of services with AssertJ - DAO and business logic is tested by initialising an in-memory H2 database with
  Exposed, using the same schema as the main app. With this approach database queries are fully tested without any
  mocking.
- Integration testing of endpoints using a fully running server with Rest Assured - routing tests/status codes/response
  structure. This utilises the fact that Ktor is a small microframework that can be easily spun up and down as part of
  the test suite. You could also use the special test engine that [Ktor provides](https://ktor.io/docs/testing.html),
  however my preference is to always start a full version of the server so that HTTP behaviour can be tested without
  relying on special internal mechanisms.
- Code coverage and reporting performed automatically by Kover as part of the Gradle build
