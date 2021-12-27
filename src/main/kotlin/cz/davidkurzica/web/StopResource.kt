package cz.davidkurzica.web

import cz.davidkurzica.model.Stop
import cz.davidkurzica.model.StopDTO
import cz.davidkurzica.service.StopService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.stop(stopService: StopService) {

    route("/stop") {

        get {
            call.respond(stopService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val stop = stopService.get(id)
            if (stop == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(stop)
        }

        post {
            val stop = call.receive<Stop>()
            call.respond(HttpStatusCode.Created, stopService.insert(stop))
        }

        put {
            val stop = call.receive<Stop>()
            val updated = stopService.update(stop)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = stopService.delete(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
