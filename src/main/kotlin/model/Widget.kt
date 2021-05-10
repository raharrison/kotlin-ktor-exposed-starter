package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Widgets : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val quantity = integer("quantity")
    val dateUpdated = long("dateUpdated")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Widget(
        val id: Int,
        val name: String,
        val quantity: Int,
        val dateUpdated: Long
)

@Serializable
data class NewWidget(
        val id: Int?,
        val name: String,
        val quantity: Int
)
