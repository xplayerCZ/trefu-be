package com.example.data.line

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LineController {
    fun getAll(): ArrayList<Line> {
        val lines: ArrayList<Line> = arrayListOf()
        transaction {
            Lines.selectAll().map {
                lines.add(
                    Line(
                        id = it[Lines.id],
                        fullCode = it[Lines.fullCode],
                        shortCode = it[Lines.shortCode]
                    )
                )
            }
        }
        return lines
    }

    fun insert(line: Line) {
        transaction {
            Lines.insert {
                it[id] = line.id!!
                it[fullCode] = line.fullCode
                it[shortCode] = line.shortCode
            }
        }
    }

    fun update(line: Line) {
        transaction {
            Lines.update({ Lines.id eq line.id!!}) {
                it[id] = line.id!!
                it[fullCode] = line.fullCode
                it[shortCode] = line.shortCode
            }
        }
    }
}