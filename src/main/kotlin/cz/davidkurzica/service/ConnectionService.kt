package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Connection
import cz.davidkurzica.model.ConnectionDTO
import cz.davidkurzica.model.Connections
import cz.davidkurzica.model.Lines
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.joda.time.LocalTime

class ConnectionService {

    suspend fun get(id: Int) = dbQuery {
        Connections.select { Connections.id eq id }.mapNotNull { toConnection(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Connections.selectAll().map { toConnection(it) }
    }

    suspend fun getByTimetable(timetableId: Int): List<Connection> = dbQuery {
        Connections.select { Connections.timetableId eq timetableId }.mapNotNull { toConnection(it) }
    }

    suspend fun insert(connection: ConnectionDTO, timetableId: Int): Connection {
        var key = 0
        dbQuery {
            key = Connections.insert {
                it[number] = connection.number
                it[notes] = connection.notes
                it[Connections.timetableId] = timetableId
                it[times] = connection.times.map { time -> time?.millisOfDay }.joinToString(";" )
            } get Connections.id
        }
        return get(key)!!
    }

    suspend fun update(connection: ConnectionDTO, timetableId: Int): Connection? {
        var exists = false
        dbQuery {
            exists = Connections.select { Connections.timetableId eq timetableId and (Connections.number eq connection.number ) }.singleOrNull() != null
        }
        return if (!exists) {
            insert(connection, timetableId)
        } else {
            return null
        }
    }

    suspend fun delete(id: Int) = dbQuery { Connections.deleteWhere { Connections.id eq id } > 0 }

    private fun toConnection(row: ResultRow) =
        Connection(
            id = row[Connections.id],
            number = row[Connections.number],
            notes = row[Connections.notes],
            times = row[Connections.times].split(";").map { if(it != "null") LocalTime(0).plusMillis(it.toInt()) else null }
        )
}