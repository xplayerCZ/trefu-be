package cz.davidkurzica.web

import cz.davidkurzica.model.Connection
import cz.davidkurzica.model.ConnectionDTO
import cz.davidkurzica.service.ConnectionService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.connection(connectionService: ConnectionService) {

    route("/connection") {

        get {
            call.respond(connectionService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val connection = connectionService.get(id)
            if (connection == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(connection)
        }

        post {
            val connection = call.receive<ConnectionDTO>()
            call.respond(HttpStatusCode.Created, connectionService.insert(connection))
        }

        put {
            val connection = call.receive<Connection>()
            val updated = connectionService.update(connection)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = connectionService.delete(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
