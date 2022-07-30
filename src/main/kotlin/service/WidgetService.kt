package service

import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbExec

class WidgetService {

    private val listeners = mutableMapOf<Int, suspend (WidgetNotification) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (WidgetNotification) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: Widget? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }

    suspend fun getAllWidgets(): List<Widget> = dbExec {
        Widgets.selectAll().map { toWidget(it) }
    }

    suspend fun getWidget(id: Int): Widget? = dbExec {
        Widgets.select {
            (Widgets.id eq id)
        }.map { toWidget(it) }
            .singleOrNull()
    }

    suspend fun updateWidget(widget: NewWidget): Widget? {
        val id = widget.id
        return if (id == null) {
            addWidget(widget)
        } else {
            dbExec {
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
        dbExec {
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
        return dbExec {
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
