package cz.davidkurzica.web

import cz.davidkurzica.model.ConnectionItem
import cz.davidkurzica.model.ConnectionItemPart
import cz.davidkurzica.model.DepartureSimple
import cz.davidkurzica.model.NewConnection
import cz.davidkurzica.service.ConnectionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalTime

fun Route.connection() {

    val connectionService: ConnectionService by inject()

    route("/connection") {

        get("/item") {
            val mockup = listOf(
                ConnectionItem(
                    listOf(
                        ConnectionItemPart(
                            lineShortCode = "209",
                            from = DepartureSimple(
                                LocalTime.of(15, 35),
                                "Horovo náměstí"
                            ),
                            to = DepartureSimple(
                                LocalTime.of(15, 40),
                                "Divadlo"
                            )
                        ),
                        ConnectionItemPart(
                            lineShortCode = "206",
                            from = DepartureSimple(
                                LocalTime.of(15, 40),
                                "Divadlo"
                            ),
                            to = DepartureSimple(
                                LocalTime.of(15, 43),
                                "Praskova"
                            )
                        ),
                        ConnectionItemPart(
                            lineShortCode = "219",
                            from = DepartureSimple(
                                LocalTime.of(15, 44),
                                "Praskova"
                            ),
                            to = DepartureSimple(
                                LocalTime.of(15, 48),
                                "Švédská kaple"
                            )
                        )
                    )
                ),
                ConnectionItem(
                    listOf(
                        ConnectionItemPart(
                            lineShortCode = "208",
                            from = DepartureSimple(
                                LocalTime.of(15, 48),
                                "Horovo náměstí"
                            ),
                            to = DepartureSimple(
                                LocalTime.of(16, 0),
                                "Švédská kaple"
                            )
                        )
                    ),
                )
            )


            call.respond(HttpStatusCode.OK, mockup)
        }

        post {
            val connection = call.receive<NewConnection>()
            call.respond(HttpStatusCode.Created, connectionService.addConnection(connection))
        }
    }
}
