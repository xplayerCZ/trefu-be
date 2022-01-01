package cz.davidkurzica.web

import cz.davidkurzica.model.Packet
import cz.davidkurzica.model.RouteDTO
import cz.davidkurzica.service.RouteService
import cz.davidkurzica.service.StopService
import cz.davidkurzica.service.TrackService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.route(routeService: RouteService) {

    route("/route") {
        post {
            val route = call.receive<RouteDTO>()
            call.respond(HttpStatusCode.Created, routeService.insert(route))
        }
    }
}