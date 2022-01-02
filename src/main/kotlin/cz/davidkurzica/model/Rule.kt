package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Rules: Table() {
    val id = integer("rule_id").autoIncrement()
    val description = text("description")

    override val primaryKey = PrimaryKey(id, name = "PK_Rules")
}

@Serializable
class NewRule(
    val id: Int,
    val description: String
)

@Serializable
class Rule(
    val id: Int,
    val description: String
)

