package cz.davidkurzica.model

import org.jetbrains.exposed.sql.Table

object Lines : Table() {
    val id = integer("id").autoIncrement()
    val shortCode = text("shortCode")
    val fullCode = text("longCode")

    override val primaryKey = PrimaryKey(id, name = "PK_Lines_ID")
}

data class Line(
    val id: Int?,
    val fullCode: String,
    val shortCode: String
)
