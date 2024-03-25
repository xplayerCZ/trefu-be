package cz.davidkurzica.domain.connection

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/connections")
class ConnectionsEndpoint(
    val offset: Int? = null,
    val limit: Int? = null,
    val routeId: Int? = null,
    val packetId: Int? = null,
)

@Serializable
@Resource("/connections/{id}")
class ConnectionByIdEndpoint(val id: Int) {

    @Serializable
    @Resource("/departures")
    class DeparturesEndpoint(val parent: ConnectionByIdEndpoint, val offset: Int? = null, val limit: Int? = null)

    @Serializable
    @Resource("/rules")
    class RulesEndpoint(val parent: ConnectionByIdEndpoint, val offset: Int? = null, val limit: Int? = null)
}

fun Route.connection() {

    val connectionService: ConnectionService by inject()

    get<ConnectionsEndpoint> {
        call.respond(
            connectionService.getConnections(
                offset = it.offset,
                limit = it.limit,
                routeId = it.routeId,
                packetId = it.packetId,
            )
        )
    }

    post<ConnectionsEndpoint> {
        try {
            val connection = call.receive<NewConnection>()
            call.respond(
                message = connectionService.addConnection(connection),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Connection is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<ConnectionByIdEndpoint> {
        val connection =
            connectionService.getConnectionById(it.id) ?: return@get call.respondText(
                "No connection with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(connection)
    }

    put<ConnectionByIdEndpoint> {
        try {
            val connection = call.receive<NewConnection>()
            connectionService.editConnection(connection, it.id)
            call.respondText("Connection with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Connection is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<ConnectionByIdEndpoint> {
        if (connectionService.deleteConnectionById(it.id)) {
            call.respondText("Connection with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText(
                "Failed to delete Connection with id ${it.id}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    get<ConnectionByIdEndpoint.DeparturesEndpoint> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }

    get<ConnectionByIdEndpoint.RulesEndpoint> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }
}
