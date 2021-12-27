package cz.davidkurzica.web

import cz.davidkurzica.model.Timetable
import cz.davidkurzica.model.TimetableDTO
import cz.davidkurzica.service.TimetableService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.timetable(timetableService: TimetableService) {

    route("/timetable") {

        get {
            call.respond(timetableService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val timetable = timetableService.get(id)
            if (timetable == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(timetable)
        }

        post {
            val timetable = call.receive<TimetableDTO>()
            call.respond(HttpStatusCode.Created, timetableService.insert(timetable))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = timetableService.delete(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
