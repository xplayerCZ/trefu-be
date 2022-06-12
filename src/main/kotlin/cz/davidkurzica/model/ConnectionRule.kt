package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object ConnectionRules: Table("connection_rules") {
    val connectionId = integer("connection_id") references Connections.id
    val ruleId = integer("rule_id") references Rules.id

    override val primaryKey = PrimaryKey(connectionId, ruleId, name = "PK_Timetables")
}

@Serializable
class ConnectionRule(
    val connectionId: Int,
    val ruleId: Int,
)