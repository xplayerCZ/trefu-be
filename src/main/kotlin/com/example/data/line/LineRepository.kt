package com.example.data.line

import org.jetbrains.exposed.sql.Table

object Lines : Table() {
    val id = integer("id").autoIncrement()
    val shortCode = text("shortCode")
    val fullCode = text("longCode")

    override val primaryKey = PrimaryKey(id, name = "PK_Lines_ID")
}