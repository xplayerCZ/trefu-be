package cz.davidkurzica.web

import cz.davidkurzica.service.RuleService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.rule() {

    val ruleService: RuleService by inject()

    route("/rule") {

    }
}