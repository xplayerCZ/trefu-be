package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Connection
import cz.davidkurzica.model.ConnectionDTO
import cz.davidkurzica.model.Connections
import org.jetbrains.exposed.sql.*
import org.joda.time.LocalTime

class ConnectionService {
    private val timetableService = TimetableService()

    suspend fun get(id: Int) = dbQuery {
        Connections.select { Connections.id eq id }.mapNotNull { toConnection(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Connections.selectAll().map { toConnection(it) }
    }

    suspend fun insert(connection: ConnectionDTO): Connection {
        var key = 0
        dbQuery {
            key = Connections.insert {
                it[number] = connection.number
                it[notes] = connection.notes
                it[timetableId] = connection.timetableId
                it[times] = connection.times.map { time -> time.millisOfSecond }.joinToString { ";" }
            } get Connections.id
        }
        return get(key)!!
    }

    suspend fun update(connection: Connection): Connection? {
        val id = connection.id
        dbQuery {
            Connections.update({ Connections.id eq id }) {
                it[number] = connection.number
                it[notes] = connection.notes
                it[timetableId] = connection.timetable.id
                it[times] = connection.times.map { time -> time.millisOfSecond }.joinToString { ";" }
            }
        }
        return get(id)
    }

    suspend fun delete(id: Int) = dbQuery { Connections.deleteWhere { Connections.id eq id } > 0 }

    private suspend fun toConnection(row: ResultRow) =
        Connection(
            id = row[Connections.id],
            number = row[Connections.number],
            notes = row[Connections.notes],
            timetable = timetableService.get(row[Connections.timetableId])!!,
            times = row[Connections.times].split(";").map { LocalTime(it.toLong()) }
        )
}