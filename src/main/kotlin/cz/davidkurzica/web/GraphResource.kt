package cz.davidkurzica.web

import cz.davidkurzica.model.NewGraph
import cz.davidkurzica.service.GraphService
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
@Resource("/graphs")
class Graphs(
    val offset: Int? = null,
    val limit: Int? = null,
    val packetId: Int? = null,
)

@Serializable
@Resource("/graphs/{id}")
class GraphById(val id: Int)

fun Route.graph() {

    val graphService: GraphService by inject()

    get<Graphs> {
        call.respond(
            graphService.getGraphs(
                offset = it.offset,
                limit = it.limit,
                packetId = it.packetId,
            )
        )
    }

    post<Graphs> {
        try {
            val graph = call.receive<NewGraph>()
            call.respond(
                message = graphService.addGraph(graph),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Graph is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<GraphById> {
        val graph =
            graphService.getGraphById(it.id) ?: return@get call.respondText(
                "No graph with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(graph)
    }

    put<GraphById> {
        try {
            val graph = call.receive<NewGraph>()
            graphService.editGraph(graph, it.id)
            call.respondText("Graph with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Graph is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<GraphById> {
        if (graphService.deleteGraphById(it.id)) {
            call.respondText("Graph with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Graph with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}