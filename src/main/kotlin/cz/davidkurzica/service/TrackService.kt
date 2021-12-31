package cz.davidkurzica.service

import cz.davidkurzica.model.Stops
import cz.davidkurzica.model.Track
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class TrackService {

    suspend fun getAll() = dbQuery {
        Stops.slice(Stops.id, Stops.name).selectAll().map { toTrack(it) }
    }

    private fun toTrack(row: ResultRow) =
        Track(
            id = row[Stops.id],
            name = row[Stops.name],
            enabled = true
        )
}