package model

import org.jetbrains.exposed.sql.Table

object Widgets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val price = decimal("price", 6, 3)
    val dateCreated = long("dateCreated")
}


data class Widget(
        val id: Int,
        val name: String,
        val price: Double,
        val dateCreated: Long
)


data class NewWidget(
        val id: Int?,
        val name: String,
        val price: Double,
        val dateCreated: Long
)
