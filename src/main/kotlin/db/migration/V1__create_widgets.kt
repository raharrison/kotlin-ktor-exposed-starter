package db.migration

import model.Widgets
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class V1__create_widgets: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Widgets)

            Widgets.insert {
                it[name] = "widget one"
                it[quantity] = 27
                it[dateUpdated] = System.currentTimeMillis()
            }
            Widgets.insert {
                it[name] = "widget two"
                it[quantity] = 14
                it[dateUpdated] = System.currentTimeMillis()
            }
        }
    }
}
