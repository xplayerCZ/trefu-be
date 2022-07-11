package cz.davidkurzica.web

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.service.PacketService
import cz.davidkurzica.util.LocalDateSerializer
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.time.LocalDate

@Serializable
@Resource("/packets")
class Packets(
    val offset: Int? = null,
    val limit: Int? = null,
    val activeAfter: @Serializable(with = LocalDateSerializer::class) LocalDate? = null,
    val activeBefore: @Serializable(with = LocalDateSerializer::class) LocalDate? = null,
    val valid: Boolean? = null,
)

@Serializable
@Resource("/packets/{id}")
class PacketById(val id: Int) {

    @Serializable
    @Resource("/lines")
    class Lines(val parent: PacketById, val offset: Int? = null, val limit: Int? = null)
}

fun Route.packet() {

    val packetService: PacketService by inject()

    get<Packets> {
        call.respond(
            packetService.getPackets(
                offset = it.offset,
                limit = it.limit,
                activeAfter = it.activeAfter,
                activeBefore = it.activeBefore,
                valid = it.valid,
            )
        )
    }

    post<Packets> {
        try {
            val packet = call.receive<NewPacket>()
            call.respond(
                message = packetService.addPacket(packet),
                status = HttpStatusCode.Created
            )
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

    delete<PacketById> {
        if (packetService.deletePacketById(it.id)) {
            call.respondText("Packet with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Packet with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }

    get<PacketById.Lines> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }
}
