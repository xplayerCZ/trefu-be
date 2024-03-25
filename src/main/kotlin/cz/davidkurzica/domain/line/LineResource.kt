package cz.davidkurzica.domain.line

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
@Resource("/lines")
class LinesEndpoint(
    val offset: Int? = null,
    val limit: Int? = null,
    val packetId: Int? = null,
)

@Serializable
@Resource("/lines/{id}")
class LineByIdEndpoint(val id: Int) {

    @Serializable
    @Resource("/routes")
    class RoutesEndpoint(val parent: LineByIdEndpoint, val offset: Int? = null, val limit: Int? = null)
}

fun Route.line() {

    val lineService: LineService by inject()

    get<LinesEndpoint> {
        call.respond(
            lineService.getLines(
                offset = it.offset,
                limit = it.limit,
                packetId = it.packetId,
            )
        )
    }

    post<LinesEndpoint> {
        try {
            val line = call.receive<NewLine>()
            call.respond(
                message = lineService.addLine(line),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("Line is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<LineByIdEndpoint> {
        val line =
            lineService.getLineById(it.id) ?: return@get call.respondText(
                "No line with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(line)
    }

    put<LineByIdEndpoint> {
        try {
            val line = call.receive<NewLine>()
            lineService.editLine(line, it.id)
            call.respondText("Line with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Line is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<LineByIdEndpoint> {
        if (lineService.deleteLineById(it.id)) {
            call.respondText("Line with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Line with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }

    get<LineByIdEndpoint.RoutesEndpoint> {
        call.respondText("Not yet implemented", status = HttpStatusCode.NotImplemented)
    }
}
