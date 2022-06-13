package cz.davidkurzica.model

import cz.davidkurzica.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Packets : Table() {
    val id = integer("packet_id").autoIncrement()
    val from = date("from")
    val to = date("to")
    val valid = bool("valid")
    val code = integer("code")

    override val primaryKey = PrimaryKey(id, name = "PK_Packets")
}

@Serializable
class Packet(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean,
    val code: Int
)

@Serializable
class NewPacket(
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean,
    val code: Int
)