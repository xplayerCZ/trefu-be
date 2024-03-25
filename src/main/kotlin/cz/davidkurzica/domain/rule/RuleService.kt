package cz.davidkurzica.domain.rule

import cz.davidkurzica.db.dbQuery
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
            date?.let { andWhere { Rules.id eq it.toRuleId() } }
        }

        query.mapNotNull { it.toRule() }
    }

    suspend fun getRuleById(id: Int): Rule? = dbQuery {
        Rules
            .select { (Rules.id eq id) }
            .mapNotNull { it.toRule() }
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

    private fun ResultRow.toRule(): Rule =
        Rule(
            id = this[Rules.id],
            description = this[Rules.description],
        )

    private fun LocalDate.toRuleId() = when (this.dayOfWeek) {
        DayOfWeek.SATURDAY -> 2
        DayOfWeek.SUNDAY -> 3
        else -> when (this.month) {
            Month.JULY, Month.AUGUST -> 4
            else -> 1
        }
    }
}
