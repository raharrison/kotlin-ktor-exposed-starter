package service

import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory {
    init {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            create(Widgets)
            Widgets.insert {
                it[name] = "widget one"
                it[quantity] = 27
                it[dateCreated] = System.currentTimeMillis()
            }
            Widgets.insert {
                it[name] = "widget two"
                it[quantity] = 14
                it[dateCreated] = System.currentTimeMillis()
            }
        }
    }
}