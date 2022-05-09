package cz.davidkurzica.web

import cz.davidkurzica.model.NewStop
import cz.davidkurzica.service.StopService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stop(stopService: StopService) {

    route("/stop") {

        get("/item") {
            call.respond(HttpStatusCode.OK, stopService.getAllDetail())
        }

        get {
            call.respond(HttpStatusCode.OK, stopService.getAll())
        }

        post {
            val stop = call.receive<NewStop>()
            call.respond(HttpStatusCode.Created, stopService.addStop(stop))
        }
    }
}
