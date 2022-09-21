package cz.davidkurzica.plugins

import cz.davidkurzica.service.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val serviceModule = module {
    single { ConnectionService() }
    single { LineService() }
    single { PacketService() }
    single { StopService() }
    single { DepartureService() }
    single { RouteService() }
    single { RuleService() }
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }
}