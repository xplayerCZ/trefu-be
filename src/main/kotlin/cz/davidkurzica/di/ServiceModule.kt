package cz.davidkurzica.di

import cz.davidkurzica.service.*
import org.koin.dsl.module

val serviceModule = module {
    single { ConnectionService() }
    single { ConnectionRuleService() }
    single { LineService() }
    single { PacketService() }
    single { StopService() }
    single { DepartureService() }
    single { RouteService() }
    single { RouteStopService() }
    single { RuleService() }
    single { GraphService() }
    single { GraphNodeService() }
    single { GraphEdgeService() }
}