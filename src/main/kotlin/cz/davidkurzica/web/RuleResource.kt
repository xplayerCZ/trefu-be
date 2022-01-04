package cz.davidkurzica.web

import cz.davidkurzica.model.NewRule
import cz.davidkurzica.service.RuleService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.rule(ruleService: RuleService) {

    route("/rule") {

        post {
            val rule = call.receive<NewRule>()
            call.respond(HttpStatusCode.Created, ruleService.addRule(rule))
        }
    }
}