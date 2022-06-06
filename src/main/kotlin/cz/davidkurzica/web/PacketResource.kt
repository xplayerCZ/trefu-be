package cz.davidkurzica.web

import cz.davidkurzica.service.PacketService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.packet() {

    val packetService: PacketService by inject()

}
