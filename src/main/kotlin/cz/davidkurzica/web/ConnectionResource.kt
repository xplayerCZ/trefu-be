package cz.davidkurzica.web

import cz.davidkurzica.model.NewConnection
import cz.davidkurzica.service.ConnectionService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.connection(connectionService: ConnectionService) {

    route("/connection") {

        post {
            val connection = call.receive<NewConnection>()
            call.respond(HttpStatusCode.Created, connectionService.addConnection(connection))
        }
    }
}
