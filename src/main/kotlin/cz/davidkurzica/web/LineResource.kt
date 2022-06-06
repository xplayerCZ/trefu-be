package cz.davidkurzica.web

import cz.davidkurzica.service.LineService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.line() {

    val lineService: LineService by inject()

}
