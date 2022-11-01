package cz.davidkurzica

import cz.davidkurzica.db.DatabaseFactory
import cz.davidkurzica.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*


fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // application.conf references the main function.
fun Application.module() {
    configureResources()
    configureHTTP()
    configureRouting()
    configureSerialization()
    configureKoin()
    configureMonitoring()

    DatabaseFactory.initDatabase(this.environment.config)
}