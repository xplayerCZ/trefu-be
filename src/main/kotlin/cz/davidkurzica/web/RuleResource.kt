package cz.davidkurzica.web

import cz.davidkurzica.model.NewRule
import cz.davidkurzica.service.RuleService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.rule(ruleService: RuleService) {

    route("/rule") {

        post {
            val rule = call.receive<NewRule>()
            call.respond(HttpStatusCode.Created, ruleService.addRule(rule))
        }
    }
}