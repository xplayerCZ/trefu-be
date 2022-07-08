package cz.davidkurzica.web

import cz.davidkurzica.model.NewRoute
import cz.davidkurzica.service.RouteService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/routes")
class Routes(
    val offset: Int?,
    val limit: Int?,
    val lineId: Int?,
    val direction: Int?,
    val packetId: Int?,
)

@Serializable
@Resource("/routes/{id}")
class RouteById(val id: Int) {

    @Serializable
    @Resource("/connections")
    class Connections(val parent: RouteById, val offset: Int? = 0, val limit: Int? = 20)

    @Serializable
    @Resource("/routes")
    class Routes(val parent: RouteById, val offset: Int? = 0, val limit: Int? = 20)
}

fun Route.route() {

    val routeService: RouteService by inject()

    get<Routes> {
        call.respond(
            routeService.getRoutes(
                offset = it.offset,
                limit = it.limit,
                lineId = it.lineId,
                direction = it.direction,
                packetId = it.packetId,
            )
        )
    }

    post<Routes> {
        try {
            val route = call.receive<NewRoute>()
            call.respond(
                message = routeService.addRoute(route),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Route is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<RouteById> {
        val route =
            routeService.getRouteById(it.id) ?: return@get call.respondText(
                "No route with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(route)
    }

    put<RouteById> {
        try {
            val route = call.receive<NewRoute>()
            routeService.editRoute(route, it.id)
            call.respondText("Route with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Route is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<RouteById> {
        if (routeService.deleteRouteById(it.id)) {
            call.respondText("Route with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Route with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }

    get<RouteById.Connections> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }

    get<RouteById.Routes> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }
}
