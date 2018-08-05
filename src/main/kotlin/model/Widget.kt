package model

import org.jetbrains.exposed.sql.Table

object Widgets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val quantity = integer("quantity")
    val dateUpdated = long("dateUpdated")
}


data class Widget(
        val id: Int,
        val name: String,
        val quantity: Int,
        val dateUpdated: Long
)


data class NewWidget(
        val id: Int?,
        val name: String,
        val quantity: Int
)
