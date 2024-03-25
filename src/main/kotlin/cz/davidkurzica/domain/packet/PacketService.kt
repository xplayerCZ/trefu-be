package cz.davidkurzica.domain.packet

import cz.davidkurzica.db.dbQuery
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

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            activeAfter?.let { andWhere { (Packets.from greaterEq it) or (Packets.to greaterEq it) } }
            activeBefore?.let { andWhere { (Packets.from lessEq it) or (Packets.to lessEq it) } }
            valid?.let { andWhere { Packets.valid eq it } }
        }

        query.mapNotNull { it.toPacket() }
    }

    suspend fun getPacketById(id: Int): Packet? = dbQuery {
        Packets.select {
            (Packets.id eq id)
        }.mapNotNull { it.toPacket() }
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

    private fun ResultRow.toPacket(): Packet =
        Packet(
            id = this[Packets.id],
            from = this[Packets.from],
            to = this[Packets.to],
            valid = this[Packets.valid],
            code = this[Packets.code]
        )
}