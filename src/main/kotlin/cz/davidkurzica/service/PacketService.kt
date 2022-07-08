package cz.davidkurzica.service

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.Packets
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import cz.davidkurzica.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class PacketService {

    suspend fun getPackets(
        offset: Int?,
        limit: Int?,
        activeAfter: @Serializable(with = LocalDateSerializer::class) LocalDate?,
        activeBefore: @Serializable(with = LocalDateSerializer::class) LocalDate?,
        valid: Boolean?,
    ) = dbQuery {
        val query = Packets.selectAll()

        limit?.let { query.limit(it, (offset ?: 0).toLong()) }
        activeAfter?.let { query.andWhere { (Packets.from greaterEq it) or (Packets.to greaterEq it) } }
        activeBefore?.let { query.andWhere { (Packets.from lessEq it) or (Packets.to lessEq it) } }
        valid?.let { query.andWhere { Packets.valid eq it } }

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

    suspend fun deletePacketById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Packets.deleteWhere { Packets.id eq id }
        }
        return numOfDeletedItems == 1
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