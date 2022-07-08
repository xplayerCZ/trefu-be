package cz.davidkurzica.web

import cz.davidkurzica.model.NewStop
import cz.davidkurzica.service.StopService
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
@Resource("/stops")
class Stops(
    val offset: Int?,
    val limit: Int?,
    val packetId: Int?,
)

@Serializable
@Resource("/stops/{id}")
class StopById(val id: Int)

fun Route.stop() {

    val stopService: StopService by inject()

    get<Stops> {
        call.respond(
            stopService.getStops(
                offset = it.offset,
                limit = it.limit,
                packetId = it.packetId,
            )
        )
    }

    post<Stops> {
        try {
            val stop = call.receive<NewStop>()
            call.respond(
                message = stopService.addStop(stop),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Stop is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<StopById> {
        val stop =
            stopService.getStopById(it.id) ?: return@get call.respondText(
                "No stop with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(stop)
    }

    put<StopById> {
        try {
            val stop = call.receive<NewStop>()
            stopService.editStop(stop, it.id)
            call.respondText("Stop with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Stop is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<StopById> {
        if (stopService.deleteStopById(it.id)) {
            call.respondText("Stop with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Stop with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}
