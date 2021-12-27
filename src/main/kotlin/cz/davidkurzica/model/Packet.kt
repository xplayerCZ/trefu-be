package cz.davidkurzica.model

import cz.davidkurzica.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.date
import org.joda.time.LocalDate

object Packets : Table() {
    val id = integer("id").autoIncrement()
    val from = date("from")
    val to = date("to")
    val valid = bool("valid")

    override val primaryKey = PrimaryKey(id, name = "PK_Packets_ID")
}

@Serializable
data class Packet(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean
)

@Serializable
data class PacketDTO(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean
)