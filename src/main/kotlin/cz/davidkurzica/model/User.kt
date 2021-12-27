package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val firstname = text("firstname")
    val lastname = text("lastname")
    val age = integer("age")

    override val primaryKey = PrimaryKey(id, name = "PK_Users_ID")
}

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int
)

@Serializable
data class UserDTO(
    val firstName: String,
    val lastName: String,
    val age: Int
)