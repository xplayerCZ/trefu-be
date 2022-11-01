package cz.davidkurzica.web

import cz.davidkurzica.model.NewGraphEdge
import cz.davidkurzica.service.GraphEdgeService
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
@Resource("/graph-edges")
class GraphEdges(
    val offset: Int? = null,
    val limit: Int? = null,
    val packetId: Int? = null,
    val graphId: Int? = null,
    val a: Int? = null,
    val b: Int? = null,
)

@Serializable
@Resource("/graph-edges/{id}")
class GraphEdgeById(val id: Int)

fun Route.graphEdge() {

    val graphEdgeService: GraphEdgeService by inject()

    get<GraphEdges> {
        call.respond(
            graphEdgeService.getGraphEdges(
                offset = it.offset,
                limit = it.limit,
                packetId = it.packetId,
                graphId = it.graphId,
                a = it.a,
                b = it.b,
            )
        )
    }

    post<GraphEdges> {
        try {
            val graphEdge = call.receive<NewGraphEdge>()
            call.respond(
                message = graphEdgeService.addGraphEdge(graphEdge),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("GraphEdge is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<GraphEdgeById> {
        val graphEdge =
            graphEdgeService.getGraphEdgeById(it.id) ?: return@get call.respondText(
                "No graphEdge with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(graphEdge)
    }

    put<GraphEdgeById> {
        try {
            val graphEdge = call.receive<NewGraphEdge>()
            graphEdgeService.editGraphEdge(graphEdge, it.id)
            call.respondText("GraphEdge with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("GraphEdge is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<GraphEdgeById> {
        if (graphEdgeService.deleteGraphEdgeById(it.id)) {
            call.respondText("GraphEdge with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete GraphEdge with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}