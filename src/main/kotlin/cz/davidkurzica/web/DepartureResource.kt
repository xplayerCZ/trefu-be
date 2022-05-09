package cz.davidkurzica.web

import cz.davidkurzica.service.DepartureService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

        get("/timetable") {
            if(!call.request.queryParameters["stopId"].isNullOrEmpty() &&
                !call.request.queryParameters["lineId"].isNullOrEmpty() &&
                !call.request.queryParameters["directionId"].isNullOrEmpty() &&
                !call.request.queryParameters["date"].isNullOrEmpty()
            ) {
                val stopId = call.request.queryParameters["stopId"]!!.toInt()
                val lineId = call.request.queryParameters["lineId"]!!.toInt()
                val directionId = call.request.queryParameters["directionId"]!!.toInt()
                val date = LocalDate.parse(call.request.queryParameters["date"]!!)

                call.respond(HttpStatusCode.OK, departureService.getTimetable(stopId, lineId, directionId, date))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}