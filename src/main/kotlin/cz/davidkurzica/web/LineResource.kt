package cz.davidkurzica.web

import cz.davidkurzica.model.NewLine
import cz.davidkurzica.service.LineService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.line(lineService: LineService) {

    route("/line") {

        post {
            val line = call.receive<NewLine>()
            call.respond(HttpStatusCode.Created, lineService.addLine(line))
        }
    }
}
