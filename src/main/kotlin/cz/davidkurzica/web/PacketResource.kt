package cz.davidkurzica.web

import cz.davidkurzica.model.NewPacket
import cz.davidkurzica.service.PacketService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

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
