package cz.davidkurzica.model

import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.LocalTime

object Connections: Table() {
    val id = integer("connection_id").autoIncrement()
    val routeId = integer("route_id") references Routes.id
    val number = integer("number")

    override val primaryKey = PrimaryKey(id, name = "PK_Connections")
}

object ConnectionRules: Table() {
    val connectionId = integer("connection_id") references Connections.id
    val ruleId = integer("rule_id") references Rules.id

    override val primaryKey = PrimaryKey(connectionId, ruleId, name = "PK_Timetables")
}

//Connection interface is responsible for Departures and ConnectionRules entries
@Serializable
class NewConnection(
    val routeId: Int,
    val number: Int,
    val departureTimes: List<@Serializable(with = LocalTimeSerializer::class) LocalTime?>,
    val ruleIds: List<Int>
)

@Serializable
class Connection(
    val id: Int,
    val route: Route,
    val number: Int,
    val departures: List<Departure>,
    val rules: List<Rule>
)