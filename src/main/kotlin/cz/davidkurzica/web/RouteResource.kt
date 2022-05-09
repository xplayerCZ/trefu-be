package cz.davidkurzica.web

import cz.davidkurzica.model.NewRoute
import cz.davidkurzica.service.RouteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.route() {

    val routeService: RouteService by inject()

    route("/route") {

        post {
            val route = call.receive<NewRoute>()
            call.respond(HttpStatusCode.Created, routeService.addRoute(route))
        }
    }

    route("/direction/item") {

        get {
            if(
                !call.request.queryParameters["lineId"].isNullOrEmpty()
            ) {
                val lineId = call.request.queryParameters["lineId"]!!.toInt()

                call.respond(HttpStatusCode.OK, routeService.getDirections(lineId))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}