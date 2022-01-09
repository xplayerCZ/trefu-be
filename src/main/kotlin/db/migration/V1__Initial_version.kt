package db.migration

import cz.davidkurzica.model.*
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class V1__Initial_version: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Connections, ConnectionRules, Departures, Lines, Packets, Routes, RouteStops, Rules, Stops)

            Rules.insert {
                it[id] = 1
                it[description] = "jede v pracovních dnech"
            }

            Rules.insert {
                it[id] = 2
                it[description] = "jede v neděli a ve státem uznané svátky"
            }

            Rules.insert {
                it[id] = 3
                it[description] = "jede v sobotu"
            }
        }
    }
}