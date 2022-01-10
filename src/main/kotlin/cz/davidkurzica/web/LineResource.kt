package cz.davidkurzica.web

import cz.davidkurzica.model.NewLine
import cz.davidkurzica.service.LineService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate
import java.time.LocalTime

fun Route.line(lineService: LineService) {

    route("/line") {

        get("/item") {
            if(
                !call.request.queryParameters["stopId"].isNullOrEmpty() &&
                !call.request.queryParameters["date"].isNullOrEmpty()
            ) {
                val stopId = call.request.queryParameters["stopId"]!!.toInt()
                val date = LocalDate.parse(call.request.queryParameters["date"]!!)

                call.respond(HttpStatusCode.OK, lineService.getDetails(stopId, date))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get {
            call.respond(HttpStatusCode.OK, lineService.getAll())
        }

        post {
            val line = call.receive<NewLine>()
            call.respond(HttpStatusCode.Created, lineService.addLine(line))
        }
    }
}
