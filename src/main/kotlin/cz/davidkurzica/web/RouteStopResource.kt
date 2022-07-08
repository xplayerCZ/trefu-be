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
    val offset: Int?,
    val limit: Int?,
    val index: Int?,
    val routeId: Int?,
    val stopId: Int?,
    val served: Boolean,
)

@Serializable
@Resource("/route-stops")
data class RouteStopDelete(
    val routeId: Int,
    val stopId: Int,
    val index: Int,
)


fun Route.routeStop() {

    val routeStopService: RouteStopService by inject()

    get<RouteStops> {
        call.respond(
            routeStopService.getRouteStops(
                offset = it.offset,
                limit = it.limit,
                index = it.index,
                routeId = it.routeId,
                stopId = it.stopId,
                served = it.served,
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

    delete<RouteStopDelete> {
        if (routeStopService.deleteRouteStop(it.routeId, it.stopId, it.index)) {
            call.respondText("RouteStop $it deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText(
                "Failed to delete RouteStop $it",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}