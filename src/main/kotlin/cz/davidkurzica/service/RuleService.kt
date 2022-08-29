package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.NewRule
import cz.davidkurzica.model.Rule
import cz.davidkurzica.model.Rules
import org.jetbrains.exposed.sql.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class RuleService {

    suspend fun getRules(
        offset: Int?,
        limit: Int?,
        date: LocalDate?,
    ) = dbQuery {
        val query = Rules.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            date?.let { andWhere { Rules.id eq getRule(it) } }
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

    private fun getRule(date: LocalDate): Int {
        return when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> 2
            DayOfWeek.SUNDAY -> 3
            else -> when (date.month) {
                Month.JULY, Month.AUGUST -> 4
                else -> 1
            }
        }
    }
}
