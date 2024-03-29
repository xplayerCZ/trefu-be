package cz.davidkurzica.plugins
import cz.davidkurzica.web.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        connection()
        connectionRule()
        line()
        packet()
        stop()
        departure()
        route()
        routeStop()
        rule()
    }
}