package cz.davidkurzica.service

import cz.davidkurzica.model.ConnectionRule
import cz.davidkurzica.model.ConnectionRules
import cz.davidkurzica.util.DatabaseFactory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class ConnectionRuleService {

    suspend fun getConnectionRules(
        offset: Int? = null,
        limit: Int? = null,
        connectionId: Int? = null,
        ruleId: Int? = null,
    ) = DatabaseFactory.dbQuery {
        val query = ConnectionRules.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }
        connectionId?.let {
            query.andWhere { ConnectionRules.connectionId eq connectionId }
        }
        ruleId?.let {
            query.andWhere { ConnectionRules.ruleId eq ruleId }
        }

        query.mapNotNull { toConnectionRule(it) }
    }

    suspend fun addConnectionRule(connectionRule: ConnectionRule): ConnectionRule {
        DatabaseFactory.dbQuery {
            ConnectionRules.insert {
                it[connectionId] = connectionRule.connectionId
                it[ruleId] = connectionRule.ruleId
            }
        }
        return getConnectionRules(
            connectionId = connectionRule.connectionId,
            ruleId = connectionRule.ruleId
        ).single()
    }

    fun toConnectionRule(row: ResultRow) =
        ConnectionRule(
            connectionId = row[ConnectionRules.connectionId],
            ruleId = row[ConnectionRules.ruleId]
        )
}