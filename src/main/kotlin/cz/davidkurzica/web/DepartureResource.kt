package cz.davidkurzica.web

import cz.davidkurzica.model.NewDeparture
import cz.davidkurzica.service.DepartureService
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
@Resource("/departures")
class Departures(val offset: Int? = 0, val limit: Int? = 20)

@Serializable
@Resource("/departures/{id}")
class DepartureById(val id: Int)

fun Route.departure() {

    val departureService: DepartureService by inject()

    get<Departures> {
        call.respond(
            departureService.getDepartures(
                offset = it.offset,
                limit = it.limit
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
}
