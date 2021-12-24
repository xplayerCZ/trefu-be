package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Connection
import cz.davidkurzica.model.Connections
import org.jetbrains.exposed.sql.*
import org.joda.time.LocalTime

class ConnectionService {
    private val lineController = LineService()

    suspend fun get(id: Int) = dbQuery {
        Connections.select { Connections.id eq id }.mapNotNull { toConnection(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Connections.selectAll().map { toConnection(it) }
    }

    suspend fun insert(connection: Connection): Connection {
        var key = 0
        dbQuery {
            key = Connections.insert {
                it[number] = number
                it[notes] = notes
                it[lineId] = connection.line.id!!
                it[duringWeekDay] = connection.duringWeekDay
                it[times] = connection.times.map { time -> time.millisOfSecond }.joinToString { ";" }
            } get Connections.id
        }
        return get(key)!!
    }

    suspend fun update(connection: Connection): Connection? {
        val id = connection.id!!
        dbQuery {
            Connections.update({ Connections.id eq id }) {
                it[number] = number
                it[notes] = notes
                it[lineId] = connection.line.id!!
                it[duringWeekDay] = connection.duringWeekDay
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
            line = lineController.get(row[Connections.lineId])!!,
            duringWeekDay = row[Connections.duringWeekDay],
            times = row[Connections.times].split(";").map { LocalTime(it) }
        )
}