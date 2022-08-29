package cz.davidkurzica.web

import cz.davidkurzica.model.NewDeparture
import cz.davidkurzica.service.DepartureService
import cz.davidkurzica.util.LocalTimeSerializer
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
import java.time.LocalTime

@Serializable
@Resource("/departures")
class Departures(
    val offset: Int? = null,
    val limit: Int? = null,
    val connectionId: Int? = null,
    val index: Int? = null,
    val after: @Serializable(with = LocalTimeSerializer::class) LocalTime? = null,
    val before: @Serializable(with = LocalTimeSerializer::class) LocalTime? = null,
    val packetId: Int? = null,
    val stopId: Int? = null,
    val ruleId: Int? = null,
)

@Serializable
@Resource("/departures/{id}")
class DepartureById(val id: Int)

fun Route.departure() {

    val departureService: DepartureService by inject()

    get<Departures> {
        call.respond(
            departureService.getDepartures(
                offset = it.offset,
                limit = it.limit,
                connectionId = it.connectionId,
                index = it.index,
                after = it.after,
                before = it.before,
                packetId = it.packetId,
                stopId = it.stopId,
                ruleId = it.ruleId,
            )
        )
    }

    post<Departures> {
        try {
            val departure = call.receive<NewDeparture>()
            call.respond(
                message = departureService.addDeparture(departure),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Departure is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<DepartureById> {
        val departure =
            departureService.getDepartureById(it.id) ?: return@get call.respondText(
                "No departure with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(departure)
    }

    put<DepartureById> {
        try {
            val departure = call.receive<NewDeparture>()
            departureService.editDeparture(departure, it.id)
            call.respondText("Departure with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Departure is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<DepartureById> {
        if (departureService.deleteDepartureById(it.id)) {
            call.respondText("Departure with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Departure with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}
