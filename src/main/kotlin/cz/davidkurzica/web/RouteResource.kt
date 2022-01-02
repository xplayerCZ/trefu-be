package cz.davidkurzica.web

import cz.davidkurzica.model.NewRoute
import cz.davidkurzica.service.RouteService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.route(routeService: RouteService) {

    route("/route") {

        post {
            val route = call.receive<NewRoute>()
            call.respond(HttpStatusCode.Created, routeService.addRoute(route))
        }
    }
}