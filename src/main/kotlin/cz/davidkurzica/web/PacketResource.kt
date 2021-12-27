package cz.davidkurzica.web

import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.PacketDTO
import cz.davidkurzica.service.PacketService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.packet(packetService: PacketService) {

    route("/packet") {

        get {
            call.respond(packetService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val packet = packetService.get(id)
            if (packet == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(packet)
        }

        post {
            val packet = call.receive<Packet>()
            call.respond(HttpStatusCode.Created, packetService.insert(packet))
        }

        put {
            val packet = call.receive<Packet>()
            val updated = packetService.update(packet)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = packetService.delete(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
