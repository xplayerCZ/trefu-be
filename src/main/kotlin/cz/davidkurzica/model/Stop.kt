package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Stops : Table() {
    val id = integer("stop_id")
    val name = text("name")
    val latitude = text("latitude")
    val longitude = text("longitude")
    val code = integer("code")

    override val primaryKey = PrimaryKey(id, name = "PK_Stops")
}

@Serializable
class NewStop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)

@Serializable
class Stop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)

@Serializable
data class StopItem(
    val id: Int,
    val name: String,
    val enabled: Boolean,
)