package service

import model.NewWidget
import model.Widget
import model.Widgets
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class WidgetService {

    suspend fun getAllWidgets(): List<Widget> = dbQuery {
        Widgets.selectAll().map { toWidget(it) }
    }

    suspend fun getWidget(id: Int): Widget? = dbQuery {
        Widgets.select {
            (Widgets.id eq id)
        }.mapNotNull { toWidget(it) }
                .singleOrNull()
    }

    suspend fun updateWidget(widget: NewWidget): Widget? {
        val id = widget.id
        return if (id == null) {
            addWidget(widget)
        } else {
            dbQuery {
                Widgets.update({ Widgets.id eq id }) {
                    it[name] = widget.name
                    it[quantity] = widget.quantity
                    it[dateCreated] = System.currentTimeMillis()
                }
            }
            getWidget(id)
        }
    }

    suspend fun addWidget(widget: NewWidget): Widget {
        var key: Int? = 0
        dbQuery {
            key = Widgets.insert {
                it[name] = widget.name
                it[quantity] = widget.quantity
                it[dateCreated] = System.currentTimeMillis()
            } get Widgets.id
        }
        return getWidget(key!!)!!
    }

    suspend fun deleteWidget(id: Int): Boolean = dbQuery {
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
