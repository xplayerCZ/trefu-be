package com.example.model

import org.jetbrains.exposed.sql.Table

object Stops : Table() {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val latitude = text("latitude")
    val longitude = text("longitude")
    val code = text("code")

    override val primaryKey = PrimaryKey(id, name = "PK_Stops_ID")
}

data class Stop(
    val id: Int?,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: String
)