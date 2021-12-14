package com.example.data.stop

import org.jetbrains.exposed.sql.Table

object Stops : Table() {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val latitude = text("latitude")
    val longitude = text("longitude")
    val code = text("code")

    override val primaryKey = PrimaryKey(id, name = "PK_Stops_ID")
}