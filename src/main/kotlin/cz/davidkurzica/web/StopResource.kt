package cz.davidkurzica.web

import cz.davidkurzica.model.NewStop
import cz.davidkurzica.service.StopService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.stop(stopService: StopService) {

    route("/stop") {

        post {
            val stop = call.receive<NewStop>()
            call.respond(HttpStatusCode.Created, stopService.addStop(stop))
        }
    }
}
