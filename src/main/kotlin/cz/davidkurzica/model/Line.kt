package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Lines : Table() {
    val shortCode = text("short_code")
    val fullCode = integer("full_code")
    val packetId = integer("packet_id") references Packets.id

    override val primaryKey = PrimaryKey(fullCode, name = "PK_Lines")
}

@Serializable
class NewLine(
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int
)

@Serializable
class Line(
    val shortCode: String,
    val fullCode: Int,
    val packet: Packet
)