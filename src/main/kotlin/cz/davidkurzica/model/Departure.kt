package cz.davidkurzica.model

import cz.davidkurzica.model.Connections.autoIncrement
import cz.davidkurzica.model.Connections.references
import cz.davidkurzica.model.Departures.autoIncrement
import cz.davidkurzica.model.Departures.nullable
import cz.davidkurzica.model.Departures.references
import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalTime


object Departures: Table() {
    val id = integer("departure_id").autoIncrement()
    val connectionId = integer("connection_id") references Connections.id
    val time = time("time").nullable()
    val index = integer("index")

    override val primaryKey = PrimaryKey(id, name = "PK_Departures")
}

@Serializable
class Departure(
    val id: Int,
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val index: Int
)