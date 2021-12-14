package com.example.data.stop

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class StopController {
    fun getAll(): ArrayList<Stop> {
        val stops: ArrayList<Stop> = arrayListOf()
        transaction {
            Stops.selectAll().map {
                stops.add(
                    Stop(
                        id = it[Stops.id],
                        name = it[Stops.name],
                        latitude = it[Stops.latitude],
                        longitude = it[Stops.longitude],
                        code = it[Stops.code]
                    )
                )
            }
        }
        return stops
    }

    fun insert(stop: Stop) {
        transaction {
            Stops.insert {
                it[id] = stop.id!!
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            }
        }
    }

    fun update(stop: Stop) {
        transaction {
            Stops.update({ Stops.id eq stop.id!!}) {
                it[id] = stop.id!!
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            }
        }
    }
}