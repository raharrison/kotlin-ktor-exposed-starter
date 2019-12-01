package service

import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class WidgetService {

    private val listeners = mutableMapOf<Int, suspend (Notification<Widget?>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (Notification<Widget?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: Widget? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }

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
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            getWidget(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun addWidget(widget: NewWidget): Widget {
        var key = 0
        dbQuery {
            key = (Widgets.insert {
                it[name] = widget.name
                it[quantity] = widget.quantity
                it[dateUpdated] = System.currentTimeMillis()
            } get Widgets.id)
        }
        return getWidget(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    suspend fun deleteWidget(id: Int): Boolean {
        return dbQuery {
            Widgets.deleteWhere { Widgets.id eq id } > 0
        }.also {
            if (it) onChange(ChangeType.DELETE, id)
        }
    }

    private fun toWidget(row: ResultRow): Widget =
        Widget(
            id = row[Widgets.id],
            name = row[Widgets.name],
            quantity = row[Widgets.quantity],
            dateUpdated = row[Widgets.dateUpdated]
        )
}
