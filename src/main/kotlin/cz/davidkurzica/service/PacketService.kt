package cz.davidkurzica.service

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.Packets
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class PacketService {

    suspend fun getPacket(id: Int): Packet? = dbQuery {
        Packets.select {
            (Packets.id eq id)
        }.mapNotNull { toPacket(it) }
            .singleOrNull()
    }

    suspend fun addPacket(packet: NewPacket): Packet {
        var key = 0
        dbQuery {
            key = (Packets.insert {
                it[id] = packet.id
                it[from] = packet.from
                it[to] = packet.to
                it[valid] = packet.valid
            } get Packets.id)
        }
        return getPacket(key)!!
    }

    private fun toPacket(row: ResultRow): Packet =
        Packet(
            id = row[Packets.id],
            from = row[Packets.from],
            to = row[Packets.to],
            valid = row[Packets.valid]
        )
}