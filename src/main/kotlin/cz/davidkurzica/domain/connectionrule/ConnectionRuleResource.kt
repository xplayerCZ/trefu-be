package cz.davidkurzica.domain.connectionrule

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/connection-rules")
class ConnectionRulesEndpoint(
    val offset: Int? = null,
    val limit: Int? = null,
    val connectionId: Int? = null,
    val ruleId: Int? = null,
)

@Serializable
@Resource("/connection-rules")
data class ConnectionRuleDeleteEndpoint(val connectionId: Int, val ruleId: Int)

fun Route.connectionRule() {

    val connectionRuleService: ConnectionRuleService by inject()

    get<ConnectionRulesEndpoint> {
        call.respond(
            connectionRuleService.getConnectionRules(
                offset = it.offset,
                limit = it.limit,
                connectionId = it.connectionId,
                ruleId = it.ruleId
            )
        )
    }

    post<ConnectionRulesEndpoint> {
        try {
            val connectionRule = call.receive<ConnectionRule>()
            call.respond(
                message = connectionRuleService.addConnectionRule(connectionRule),
                status = HttpStatusCode.Created
            )
        } catch (e: ContentTransformationException) {
            call.respondText("ConnectionRule is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    delete<ConnectionRuleDeleteEndpoint> {
        if (connectionRuleService.deleteConnectionRule(it.connectionId, it.ruleId)) {
            call.respondText("ConnectionRule $it deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText(
                "Failed to delete ConnectionRule $it",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}