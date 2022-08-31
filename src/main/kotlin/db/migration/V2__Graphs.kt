package db.migration

import cz.davidkurzica.model.GraphEdges
import cz.davidkurzica.model.GraphNodes
import cz.davidkurzica.model.Graphs
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ClassName", "unused")
class V2__Graphs : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(
                Graphs,
                GraphNodes,
                GraphEdges
            )
        }
    }
}