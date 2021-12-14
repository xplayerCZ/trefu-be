package com.example.data.packet

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.joda.time.LocalDate

class PacketController {
    fun getAll(): ArrayList<Packet> {
        val packets: ArrayList<Packet> = arrayListOf()
        transaction {
            Packets.selectAll().map {
                packets.add(
                    Packet(
                        id = it[Packets.id],
                        from = LocalDate(it[Packets.from]),
                        to = LocalDate(it[Packets.to]),
                        valid = it[Packets.valid]
                    )
                )
            }
        }
        return packets
    }

    fun insert(packet: Packet) {
        transaction {
            Packets.insert {
                it[id] = packet.id!!
                it[from] = DateTime(packet.from)
                it[to] = DateTime(packet.to)
                it[valid] = packet.valid
            }
        }
    }

    fun update(packet: Packet) {
        transaction {
            Packets.update({ Packets.id eq packet.id!!}) {
                it[id] = packet.id!!
                it[from] = DateTime(packet.from)
                it[to] = DateTime(packet.to)
                it[valid] = packet.valid
            }
        }
    }
}