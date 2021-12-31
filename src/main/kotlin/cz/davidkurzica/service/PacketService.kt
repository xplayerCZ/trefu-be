package cz.davidkurzica.service

import cz.davidkurzica.model.Lines
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.PacketDTO
import cz.davidkurzica.model.Packets
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


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
                it[id] = packet.id
                it[from] = packet.from
                it[to] = packet.to
                it[valid] = packet.valid
            } get Packets.id
        }
        return get(key)!!
    }

    suspend fun update(packet: Packet): Packet? {
        val id = packet.id
        var exists = false
        dbQuery {
            exists = Packets.select { Packets.id eq id }.singleOrNull() != null
        }
        return if (!exists) {
            insert(packet)
        } else {
            return null
        }
    }

    suspend fun delete(id: Int) = dbQuery { Packets.deleteWhere { Packets.id eq id } > 0 }

    private fun toPacket(row: ResultRow) =
        Packet(
            id = row[Packets.id],
            from = row[Packets.from],
            to = row[Packets.to],
            valid = row[Packets.valid]
        )
}