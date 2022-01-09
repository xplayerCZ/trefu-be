package cz.davidkurzica.web

import cz.davidkurzica.service.DepartureService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate
import java.time.LocalTime

fun Route.departure(departureService: DepartureService) {

    route("/departure") {

        get("/item") {
            if(!call.request.queryParameters["time"].isNullOrEmpty() &&
                !call.request.queryParameters["stopId"].isNullOrEmpty() &&
                !call.request.queryParameters["date"].isNullOrEmpty()
            ) {
                val time = LocalTime.parse(call.request.queryParameters["time"]!!)
                val stopId = call.request.queryParameters["stopId"]!!.toInt()
                val date = LocalDate.parse(call.request.queryParameters["date"]!!)

                call.respond(HttpStatusCode.OK, departureService.get(time, stopId, date))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}