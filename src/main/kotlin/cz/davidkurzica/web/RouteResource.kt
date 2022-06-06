package cz.davidkurzica.web

import cz.davidkurzica.service.RouteService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.route() {

    val routeService: RouteService by inject()

}