package cz.davidkurzica.domain.connectionrule

import cz.davidkurzica.db.dbQuery
import org.jetbrains.exposed.sql.*

class ConnectionRuleService {

    suspend fun getConnectionRules(
        offset: Int? = null,
        limit: Int? = null,
        connectionId: Int? = null,
        ruleId: Int? = null,
    ) = dbQuery {
        val query = ConnectionRules.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            connectionId?.let { andWhere { ConnectionRules.connectionId eq it } }
            ruleId?.let { andWhere { ConnectionRules.ruleId eq it } }
        }

        query.mapNotNull { it.toConnectionRule() }
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

    private fun ResultRow.toConnectionRule() =
        ConnectionRule(
            connectionId = this[ConnectionRules.connectionId],
            ruleId = this[ConnectionRules.ruleId]
        )
}