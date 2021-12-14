package com.example.data.packet

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.date

object Packets : Table() {
    val id = integer("id").autoIncrement()
    val from = date("from")
    val to = date("to")
    val valid = bool("valid")

    override val primaryKey = PrimaryKey(id, name = "PK_Packets_ID")
}