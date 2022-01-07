package cz.davidkurzica.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.Connection.TRANSACTION_REPEATABLE_READ
import java.util.*

object DatabaseFactory {
    private val log = LoggerFactory.getLogger(this::class.java)

    private fun loadConfigProperties(config: ApplicationConfig) =
        Properties().apply {
            setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
            setProperty("dataSource.user", config.propertyOrNull("storage.db.user")?.getString() ?: "")
            setProperty("dataSource.password", config.propertyOrNull("storage.db.password")?.getString() ?: "")
            setProperty("dataSource.databaseName", config.propertyOrNull("storage.db.databaseName")?.getString() ?: "")
            setProperty("dataSource.portNumber", config.propertyOrNull("storage.db.portNumber")?.getString() ?: "")
            setProperty("dataSource.serverName", config.propertyOrNull("storage.db.serverName")?.getString() ?: "")
        }

    fun initDatabase(config: ApplicationConfig) {
        log.info("Initialising database")
        val pool = createHikariDataSource(loadConfigProperties(config))
        Database.connect(pool)
        runFlyway(pool)
    }

    private fun createHikariDataSource(
        properties: Properties
    ) = HikariDataSource(HikariConfig(properties).apply {
        schema = "public"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    private fun runFlyway(datasource: HikariDataSource) {
        val flyway = Flyway.configure()
            .dataSource(datasource)
            .load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running flyway db.migration", e)
            throw e
        }
        log.info("Flyway db.migration has finished")
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T = newSuspendedTransaction(transactionIsolation = TRANSACTION_REPEATABLE_READ) { block() }
}