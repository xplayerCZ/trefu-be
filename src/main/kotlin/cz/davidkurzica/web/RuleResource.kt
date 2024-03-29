package cz.davidkurzica.web

import cz.davidkurzica.model.NewRule
import cz.davidkurzica.service.RuleService
import cz.davidkurzica.util.LocalDateSerializer
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
import java.time.LocalDate

@Serializable
@Resource("/rules")
class Rules(
    val offset: Int? = null,
    val limit: Int? = null,
    val date: @Serializable(with = LocalDateSerializer::class) LocalDate? = null,
)

@Serializable
@Resource("/rules/{id}")
class RuleById(val id: Int)

fun Route.rule() {

    val ruleService: RuleService by inject()

    get<Rules> {
        call.respond(
            ruleService.getRules(
                offset = it.offset,
                limit = it.limit,
                date = it.date,
            )
        )
    }

    post<Rules> {
        try {
            val rule = call.receive<NewRule>()
            call.respond(
                message = ruleService.addRule(rule),
                status = HttpStatusCode.Created
            )
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

    delete<RuleById> {
        if (ruleService.deleteRuleById(it.id)) {
            call.respondText("Rule with id ${it.id} deleted successfully", status = HttpStatusCode.OK)
        } else {
            call.respondText("Failed to delete Rule with id ${it.id}", status = HttpStatusCode.InternalServerError)
        }
    }
}
