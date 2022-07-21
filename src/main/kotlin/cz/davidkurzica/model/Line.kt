package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Lines : Table() {
    val id = integer("line_id").autoIncrement()
    val shortCode = text("short_code")
    val fullCode = integer("full_code")
    val packetId = integer("packet_id") references Packets.id

    override val primaryKey = PrimaryKey(id, name = "PK_Lines")
}

@Serializable
class Line(
    val id: Int,
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int,
)

@Serializable
class NewLine(
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int,
)


@Serializable
class LineItem(
    val id: Int,
    val shortCode: String,
)