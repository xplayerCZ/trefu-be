package cz.davidkurzica.web

import cz.davidkurzica.model.NewLine
import cz.davidkurzica.service.LineService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

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
