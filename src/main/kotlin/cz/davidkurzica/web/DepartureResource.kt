package cz.davidkurzica.web

import cz.davidkurzica.model.Departure
import cz.davidkurzica.model.DepartureDTO
import cz.davidkurzica.model.Line
import cz.davidkurzica.model.Stop
import cz.davidkurzica.service.DepartureService
import cz.davidkurzica.service.PacketService
import cz.davidkurzica.util.LocalTimeSerializer
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalTime

fun Route.departure(departureService: DepartureService) {

    route("/departure") {
       get {
           /*
           val mockupList = listOf(
               DepartureDTO(LocalTime.of(10, 30), 208, "Englišova"),
               DepartureDTO(LocalTime.of(10, 34), 205, "Jaktař"),
               DepartureDTO(LocalTime.of(10, 35), 203, "Globus")
           )
*/
           if(!call.request.queryParameters["time"].isNullOrEmpty() && !call.request.queryParameters["stopId"].isNullOrEmpty()) {
               val time = Json.decodeFromString(LocalTimeSerializer, "\"${call.request.queryParameters["time"]!!}\"")
               val stopId = Json.decodeFromString<Int>("\"${call.request.queryParameters["stopId"]!!}\"")
               call.respond(departureService.getByTimeAndStopId(time, stopId))
           } else {
               call.respond(HttpStatusCode.NotFound)
           }
       }

        post {
            val departure = call.receive<DepartureDTO>()
            call.respond(HttpStatusCode.Created, departureService.insert(departure))
        }
    }
}