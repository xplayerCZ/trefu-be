package db.migration

import cz.davidkurzica.domain.connection.Connections
import cz.davidkurzica.domain.connectionrule.ConnectionRules
import cz.davidkurzica.domain.departure.Departures
import cz.davidkurzica.domain.line.Lines
import cz.davidkurzica.domain.packet.Packets
import cz.davidkurzica.domain.route.Routes
import cz.davidkurzica.domain.routestop.RouteStops
import cz.davidkurzica.domain.rule.Rules
import cz.davidkurzica.domain.stop.Stops
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ClassName", "unused")
class V1__Initial_version : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(
                Connections,
                ConnectionRules,
                Departures,
                Lines,
                Packets,
                Routes,
                RouteStops,
                Rules,
                Stops
            )

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

            Rules.insert {
                it[id] = 4
                it[description] = "jede v pracovních dnech 1.7.- 31.8."
            }
        }
    }
}