package cz.davidkurzica.web

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.service.PacketService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.packet(packetService: PacketService) {

    route("/packet") {
        get {
            call.respond(HttpStatusCode.OK, packetService.getAll())
        }

        post {
            val packet = call.receive<NewPacket>()
            call.respond(HttpStatusCode.Created, packetService.addPacket(packet))
        }
    }
}
