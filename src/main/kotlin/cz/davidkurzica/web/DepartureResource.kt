package cz.davidkurzica.web

import cz.davidkurzica.service.DepartureService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.departure(departureService: DepartureService) {

    route("/departure") {


    }
}