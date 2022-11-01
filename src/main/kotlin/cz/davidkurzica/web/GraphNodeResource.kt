package cz.davidkurzica.web

import cz.davidkurzica.model.NewGraphNode
import cz.davidkurzica.service.GraphNodeService
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
@Resource("/graph-nodes")
class GraphNodes(
    val offset: Int? = null,
    val limit: Int? = null,
    val stopId: Int? = null,
    val graphId: Int? = null,
    val packetId: Int? = null,
)

@Serializable
@Resource("/graph-nodes/{id}")
class GraphNodeById(val id: Int)

fun Route.graphNode() {

    val graphNodeService: GraphNodeService by inject()

    get<GraphNodes> {
        call.respond(
            graphNodeService.getGraphNodes(
                offset = it.offset,
                limit = it.limit,
                packetId = it.packetId,
                stopId = it.stopId,
                graphId = it.graphId
            )
        )
    }

    post<GraphNodes> {
        try {
            val graphNode = call.receive<NewGraphNode>()
            call.respond(
                message = graphNodeService.addGraphNode(graphNode),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("GraphNode is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<GraphNodeById> {
        val graphNode =
            graphNodeService.getGraphNodeById(it.id) ?: return@get call.respondText(
                "No GraphNode with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(graphNode)
    }

    put<GraphNodeById> {
        try {
            val graphNode = call.receive<NewGraphNode>()
            graphNodeService.editGraphNode(graphNode, it.id)
            call.respondText("GraphNode with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("GraphNode is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<GraphNodeById> {
        if (graphNodeService.deleteGraphNodeById(it.id)) {
            call.respondText("GraphNode with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete GraphNode with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}