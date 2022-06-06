package cz.davidkurzica.service

import cz.davidkurzica.model.NewRule
import cz.davidkurzica.model.Rule
import cz.davidkurzica.model.Rules
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class RuleService {

    private suspend fun getRule(id: Int): Rule? = dbQuery {
        Rules.select {
            (Rules.id eq id)
        }.mapNotNull { toRule(it) }
            .singleOrNull()
    }

    suspend fun addRule(rule: NewRule): Rule {
        var key = 0
        dbQuery {
            key = (Rules.insert {
                it[id] = rule.id
                it[description] = rule.description
            } get Rules.id)
        }
        return getRule(key)!!
    }

    private fun toRule(row: ResultRow): Rule =
        Rule(
            id = row[Rules.id],
            description = row[Rules.description],
        )
}
