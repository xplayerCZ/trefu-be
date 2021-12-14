package com.example.data.user

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val firstname = text("firstname")
    val lastname = text("lastname")
    val age = integer("age")

    override val primaryKey = PrimaryKey(id, name = "PK_Users_ID")
}