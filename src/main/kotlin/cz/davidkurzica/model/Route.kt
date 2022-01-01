package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Routes: Table() {
    val id = integer("id").autoIncrement()
    val length = integer("length")

    override val primaryKey = PrimaryKey(id, name = "PK_Route_ID")
}

@Serializable
data class RouteDTO(
    val stopIds: List<Int>,
    val direction: Int,
    val lineFullCode: Int,
    val packetId: Int
)

