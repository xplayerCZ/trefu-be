package com.example.model

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

data class Packet(
    val id: Int?,
    val from: LocalDate,
    val to: LocalDate,
    val valid: Boolean
)