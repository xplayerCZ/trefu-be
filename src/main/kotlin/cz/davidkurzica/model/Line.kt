package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Lines : Table() {
    val shortCode = text("shortCode")
    val fullCode = integer("fullCode")

    override val primaryKey = PrimaryKey(fullCode, name = "PK_Lines_ID")
}

@Serializable
data class Line(
    val fullCode: Int,
    val shortCode: String
)

@Serializable
data class LineDTO(
    val fullCode: Int,
    val shortCode: String
)