## Starter project to create a simple RESTful web service in Kotlin

**Updated for Kotlin 1.3.61 and Ktor 1.3.0**

Companion article: <https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html>

### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Jackson](https://github.com/FasterXML/jackson) - JSON serialization/deserialization
 - [JUnit 5](https://junit.org/junit5/), [AssertJ](http://joel-costigliola.github.io/assertj/) and [Rest Assured](http://rest-assured.io/) for testing
 
The starter project creates a new in-memory H2 database with one table for `Widget` instances.

As ktor is async and based on coroutines, standard blocking JDBC may cause performance issues when used
directly on the main thread pool (as threads must be reused for other requests). Therefore, another dedicated thread
pool is created for all database queries, alongside connection pooling with HikariCP. 

### Routes:

`GET /widget` --> get all widgets in the database

`GET /widget/{id}` --> get one widget instance by id (integer)

`POST /widget` --> add a new widget to the database by providing a JSON object (converted to a NewWidget instance).
e.g - 

    {
        "name": "new widget",
        "quantity": 64
    }

returns

    {
        "id": 4,
        "name": "new widget",
        "quantity": 64,
        "dateCreated": 1519926898
    }
    
    
`PUT /widget` --> update an existing widgets name or quantity. Pass in the id in the JSON request to determine which record to update

`DELETE /widget/{id}` --> delete the widget with the specified id

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

### Testing

The sample Widget service and corresponding endpoints are also tested with 100% coverage. Upon startup of the main JUnit suite (via the `test` source folder), the server is started ready for testing and is torn down after all tests are run.

- Unit testing of services with AssertJ - DAO and business logic
- Integration testing of endpoints using running server with Rest Assured - routing tests/status codes/response structure
