package cz.davidkurzica.web

import cz.davidkurzica.service.RuleService
import cz.davidkurzica.model.NewRule
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlinx.serialization.*

@Serializable
@Resource("/rules")
class Rules(val offset: Int? = 0, val limit: Int? = 20)

@Serializable
@Resource("/rules/{id}")
class RuleById(val id: Int)

fun Route.rule() {

    val ruleService: RuleService by inject()

    get<Rules> {
        call.respond(ruleService.getRules(
            offset = it.offset,
            limit = it.limit
        ))
    }

    post<Rules> {
        try {
            val rule = call.receive<NewRule>()
            ruleService.addRule(rule)
            call.respondText("Rule stored correctly", status = HttpStatusCode.Created)
        } catch (e: ContentTransformationException) {
            call.respondText("Rule is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }

    get<RuleById> {
        val rule =
            ruleService.getRuleById(it.id) ?: return@get call.respondText(
                "No rule with id ${it.id}",
                status = HttpStatusCode.NotFound
            )
        call.respond(rule)
    }

    put<RuleById> {
        try {
            val rule = call.receive<NewRule>()
            ruleService.editRule(rule, it.id)
            call.respondText("Rule with id ${it.id} updated correctly", status = HttpStatusCode.OK)
        } catch (e: ContentTransformationException) {
            call.respondText("Rule is in wrong format", status = HttpStatusCode.BadRequest)
        }
    }
}
