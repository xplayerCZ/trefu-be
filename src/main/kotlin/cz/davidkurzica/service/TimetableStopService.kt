package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.TimetableStopDTO
import cz.davidkurzica.model.TimetableStops
import cz.davidkurzica.model.Timetables
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TimetableStopService()
{
    suspend fun get(timetableId: Int, stopId: Int) = dbQuery {
        TimetableStops.select { TimetableStops.stopId eq stopId and (TimetableStops.timetableId eq timetableId) }.mapNotNull { toTimetableStop(it) }.singleOrNull()
    }

    suspend fun getByStops(stopId: Int) = dbQuery {
        TimetableStops.select { TimetableStops.stopId eq stopId }.mapNotNull { toTimetableStop(it) }
    }

    suspend fun getByTimetables(timetableId: Int): List<TimetableStopDTO> = dbQuery {
        TimetableStops.select { TimetableStops.timetableId eq timetableId }.mapNotNull { toTimetableStop(it) }
    }

    suspend fun insert(timetableStop: TimetableStopDTO) {
        dbQuery {
            TimetableStops.insert {
                it[stopId] = timetableStop.stopId
                it[timetableId] = timetableStop.timetableId
            }
        }
    }

    suspend fun delete(timetableId: Int, stopId: Int) = dbQuery { TimetableStops.deleteWhere { TimetableStops.stopId eq stopId and (TimetableStops.timetableId eq timetableId) } > 0 }

    private fun toTimetableStop(row: ResultRow) =
        TimetableStopDTO(
            timetableId = row[TimetableStops.timetableId],
            stopId = row[TimetableStops.stopId]
        )
}