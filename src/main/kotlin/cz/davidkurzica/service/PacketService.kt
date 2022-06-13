package cz.davidkurzica.service

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.Packets
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class PacketService {

    suspend fun getPackets(
        offset: Int?,
        limit: Int?
    ) = dbQuery {
        val query = Packets.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }

        query.mapNotNull { toPacket(it) }
    }

    suspend fun getPacketById(id: Int): Packet? = dbQuery {
        Packets.select {
            (Packets.id eq id)
        }.mapNotNull { toPacket(it) }
            .singleOrNull()
    }

    suspend fun addPacket(packet: NewPacket): Packet {
        var key = 0
        dbQuery {
            key = (Packets.insert {
                it[from] = packet.from
                it[to] = packet.to
                it[valid] = packet.valid
                it[code] = packet.code
            } get Packets.id)
        }
        return getPacketById(key)!!
    }

    suspend fun editPacket(packet: NewPacket, id: Int): Packet {
        dbQuery {
            Packets.update({ Packets.id eq id }) {
                it[from] = packet.from
                it[to] = packet.to
                it[valid] = packet.valid
                it[code] = packet.code
            }
        }
        return getPacketById(id)!!
    }

    fun toPacket(row: ResultRow): Packet =
        Packet(
            id = row[Packets.id],
            from = row[Packets.from],
            to = row[Packets.to],
            valid = row[Packets.valid],
            code = row[Packets.code]
        )
}