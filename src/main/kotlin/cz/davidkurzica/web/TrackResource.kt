package cz.davidkurzica.web

import cz.davidkurzica.service.StopService
import cz.davidkurzica.service.TrackService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.track(trackService: TrackService) {

    route("/track") {
        get {
            call.respond(trackService.getAll())
        }
    }
}