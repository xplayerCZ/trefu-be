package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.Packets
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.joda.time.LocalDate

class PacketService {

    suspend fun get(id: Int) = dbQuery {
        Packets.select { Packets.id eq id }.mapNotNull { toPacket(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Packets.selectAll().map { toPacket(it) }
    }


    suspend fun insert(packet: Packet): Packet {
        var key = 0
        dbQuery {
            key = Packets.insert {
                it[from] = DateTime(packet.from)
                it[to] = DateTime(packet.to)
                it[valid] = packet.valid
            } get Packets.id
        }
        return get(key)!!
    }

    suspend fun update(packet: Packet): Packet? {
        val id = packet.id!!
        dbQuery {
            Packets.update({ Packets.id eq id }) {
                it[from] = DateTime(packet.from)
                it[to] = DateTime(packet.to)
                it[valid] = packet.valid
            }
        }
        return get(id)
    }

    suspend fun delete(id: Int) = dbQuery { Packets.deleteWhere { Packets.id eq id } > 0 }

    private fun toPacket(row: ResultRow) =
        Packet(
            id = row[Packets.id],
            from = LocalDate(row[Packets.from]),
            to = LocalDate(row[Packets.to]),
            valid = row[Packets.valid]
        )
}