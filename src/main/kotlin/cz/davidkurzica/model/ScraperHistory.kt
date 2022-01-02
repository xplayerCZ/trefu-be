package cz.davidkurzica.model

import org.jetbrains.exposed.sql.Table

object ScraperHistory: Table() {
    val entry = integer("scraper_history_entry").autoIncrement()

    override val primaryKey = PrimaryKey(entry, name = "PK_ScraperHistory")
}