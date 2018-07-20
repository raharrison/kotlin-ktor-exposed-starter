## Starter project to create a simple RESTful web service in Kotlin

Companion article: <https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html>

### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Jackson](https://github.com/FasterXML/jackson) - JSON serialization/deserialization
 
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
