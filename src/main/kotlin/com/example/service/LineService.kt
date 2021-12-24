package com.example.service

import com.example.db.DatabaseFactory.dbQuery
import com.example.model.Line
import com.example.model.Lines
import org.jetbrains.exposed.sql.*

class LineService {

    suspend fun get(id: Int) = dbQuery {
        Lines.select { Lines.id eq id }.mapNotNull { toLine(it) }.singleOrNull()
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
            } get Lines.id
        }
        return get(key)!!
    }

    suspend fun update(line: Line): Line? {
        val id = line.id!!
        dbQuery {
            Lines.update({ Lines.id eq id }) {
                it[fullCode] = line.fullCode
                it[shortCode] = line.shortCode
            }
        }
        return get(id)
    }

    suspend fun delete(id: Int) = dbQuery { Lines.deleteWhere { Lines.id eq id } > 0 }

    private fun toLine(row: ResultRow) =
        Line(
            id = row[Lines.id],
            fullCode = row[Lines.fullCode],
            shortCode = row[Lines.shortCode]
        )
}