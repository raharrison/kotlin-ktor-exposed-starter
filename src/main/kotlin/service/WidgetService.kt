package service

import model.NewWidget
import model.Widget
import model.Widgets
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class WidgetService {

    fun getAllWidgets(): List<Widget> = transaction {
        Widgets.selectAll().map { toWidget(it) }
    }

    fun getWidget(id: Int): Widget? = transaction {
        Widgets.select {
            (Widgets.id eq id)
        }.mapNotNull { toWidget(it) }
                .singleOrNull()
    }

    fun updateWidget(widget: NewWidget): Widget {
        val id = widget.id
        return if (id == null) {
            addWidget(widget)
        } else {
            transaction {
                Widgets.update({ Widgets.id eq id }) {
                    it[name] = widget.name
                    it[quantity] = widget.quantity
                    it[dateCreated] = System.currentTimeMillis()
                }
                getWidget(id)!!
            }
        }
    }

    fun addWidget(widget: NewWidget): Widget = transaction {
        val key = Widgets.insert({
            it[name] = widget.name
            it[quantity] = widget.quantity
            it[dateCreated] = System.currentTimeMillis()
        }) get Widgets.id

        getWidget(key!!)!!
    }

    fun deleteWidget(id: Int): Boolean = transaction {
        Widgets.deleteWhere { Widgets.id eq id } > 0
    }

    private fun toWidget(row: ResultRow): Widget =
            Widget(
                    id = row[Widgets.id],
                    name = row[Widgets.name],
                    quantity = row[Widgets.quantity],
                    dateCreated = row[Widgets.dateCreated]
            )
}
