package cz.davidkurzica.web

import cz.davidkurzica.service.PacketService
import cz.davidkurzica.model.NewPacket
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlinx.serialization.*

@Serializable
@Resource("/packets")
class Packets(val offset: Int? = 0, val limit: Int? = 20)

@Serializable
@Resource("/packets/{id}")
class PacketById(val id: Int) {

    @Serializable
    @Resource("/lines")
    class Lines(val parent: PacketById, val offset: Int? = 0, val limit: Int? = 20)
}

fun Route.packet() {

    val packetService: PacketService by inject()

    get<Packets> {
        call.respond(packetService.getPackets(
            offset = it.offset,
            limit = it.limit
        ))
    }

    post<Packets> {
        try {
            val packet = call.receive<NewPacket>()
            packetService.addPacket(packet)
            call.respondText("Packet stored correctly", status = HttpStatusCode.Created)
        } catch (e: ContentTransformationException) {
            call.respondText("Packet is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<PacketById> {
        val packet =
            packetService.getPacketById(it.id) ?: return@get call.respondText(
                "No packet with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(packet)
    }

    put<PacketById> {
        try {
            val packet = call.receive<NewPacket>()
            packetService.editPacket(packet, it.id)
            call.respondText("Packet with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Packet is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get <PacketById.Lines> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }
}
