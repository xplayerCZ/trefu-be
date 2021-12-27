package cz.davidkurzica.web

import cz.davidkurzica.model.Line
import cz.davidkurzica.model.LineDTO
import cz.davidkurzica.service.LineService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.line(lineService: LineService) {

    route("/line") {

        get {
            call.respond(lineService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val line = lineService.get(id)
            if (line == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(line)
        }

        post {
            val line = call.receive<Line>()
            call.respond(HttpStatusCode.Created, lineService.insert(line))
        }

        put {
            val line = call.receive<Line>()
            val updated = lineService.update(line)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = lineService.delete(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
