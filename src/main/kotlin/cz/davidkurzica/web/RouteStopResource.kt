package cz.davidkurzica.web

import cz.davidkurzica.model.RouteStop
import cz.davidkurzica.service.RouteStopService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/route-stops")
class RouteStops(
    val routeId: Int? = null,
    val stopId: Int? = null,
    val offset: Int? = 0,
    val limit: Int? = 20
)

fun Route.routeStop() {

    val routeStopService: RouteStopService by inject()

    get<RouteStops> {
        call.respond(
            routeStopService.getRouteStops(
                offset = it.offset,
                limit = it.limit,
                routeId = it.routeId,
                stopId = it.stopId
            )
        )
    }

    post<RouteStops> {
        try {
            val routeStop = call.receive<RouteStop>()
            call.respond(
                message = routeStopService.addRouteStop(routeStop),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("RouteStop is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

}