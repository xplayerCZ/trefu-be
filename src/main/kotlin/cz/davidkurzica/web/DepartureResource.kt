package cz.davidkurzica.web

import cz.davidkurzica.service.DepartureService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.departure() {

    val departureService: DepartureService by inject()

}