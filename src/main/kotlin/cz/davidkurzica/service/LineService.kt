package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Line
import cz.davidkurzica.model.Lines
import org.jetbrains.exposed.sql.*

class LineService {

    suspend fun get(fullCode: Int) = dbQuery {
        Lines.select { Lines.fullCode eq fullCode }.mapNotNull { toLine(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Lines.selectAll().map { toLine(it) }
    }

    suspend fun insert(line: Line): Line {
        var key = 0
        dbQuery {
            key = Lines.insert {
                it[fullCode] = line.fullCode
                it[shortCode] = line.shortCode
            } get Lines.fullCode
        }
        return get(key)!!
    }

    suspend fun update(line: Line): Line? {
        val fullCode = line.fullCode
        var exists = false
        dbQuery {
            exists = Lines.select { Lines.fullCode eq fullCode }.singleOrNull() != null
        }
        return if (!exists) {
            insert(line)
        } else {
            return null
        }
    }

    suspend fun delete(fullCode: Int) = dbQuery { Lines.deleteWhere { Lines.fullCode eq fullCode } > 0 }

    private fun toLine(row: ResultRow) =
        Line(
            fullCode = row[Lines.fullCode],
            shortCode = row[Lines.shortCode]
        )
}