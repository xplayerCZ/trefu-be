package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.NewRule
import cz.davidkurzica.model.Rule
import cz.davidkurzica.model.Rules
import org.jetbrains.exposed.sql.*

class RuleService {

    suspend fun getRules(
        offset: Int?,
        limit: Int?,
    ) = dbQuery {
        val query = Rules.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
        }

        query.mapNotNull { toRule(it) }
    }

    suspend fun getRuleById(id: Int): Rule? = dbQuery {
        Rules.select {
            (Rules.id eq id)
        }.mapNotNull { toRule(it) }
            .singleOrNull()
    }

    suspend fun addRule(rule: NewRule): Rule {
        var key = 0
        dbQuery {
            key = (Rules.insert {
                it[description] = rule.description
            } get Rules.id)
        }
        return getRuleById(key)!!
    }

    suspend fun editRule(rule: NewRule, id: Int): Rule {
        dbQuery {
            Rules.update({ Rules.id eq id }) {
                it[description] = rule.description
            }
        }
        return getRuleById(id)!!
    }

    suspend fun deleteRuleById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Rules.deleteWhere { Rules.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toRule(row: ResultRow): Rule =
        Rule(
            id = row[Rules.id],
            description = row[Rules.description],
        )
}
