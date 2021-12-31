package cz.davidkurzica.web

import cz.davidkurzica.model.Departure
import cz.davidkurzica.model.DepartureDTO
import cz.davidkurzica.model.Line
import cz.davidkurzica.model.Stop
import cz.davidkurzica.service.DepartureService
import cz.davidkurzica.service.PacketService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
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
           call.respond(mockupList)

            */
       }

        post {
            val departure = call.receive<DepartureDTO>()
            call.respond(HttpStatusCode.Created, departureService.insert(departure))
        }
    }
}