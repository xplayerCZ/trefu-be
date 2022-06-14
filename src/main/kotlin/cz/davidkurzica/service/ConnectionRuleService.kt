package cz.davidkurzica.service

import cz.davidkurzica.model.ConnectionRule
import cz.davidkurzica.model.ConnectionRules
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class ConnectionRuleService {

    suspend fun getConnectionRules(
        offset: Int? = null,
        limit: Int? = null,
        connectionId: Int? = null,
        ruleId: Int? = null,
    ) = dbQuery {
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
        dbQuery {
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

    suspend fun deleteConnectionRule(connectionId: Int, ruleId: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = ConnectionRules.deleteWhere {
                (ConnectionRules.connectionId eq connectionId)
                    .and(ConnectionRules.ruleId eq ruleId)
            }
        }
        return numOfDeletedItems == 1
    }

    fun toConnectionRule(row: ResultRow) =
        ConnectionRule(
            connectionId = row[ConnectionRules.connectionId],
            ruleId = row[ConnectionRules.ruleId]
        )
}