package cz.davidkurzica.web

import cz.davidkurzica.service.StopService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.stop() {

    val stopService: StopService by inject()

}
