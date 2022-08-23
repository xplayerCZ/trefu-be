package cz.davidkurzica.db

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.Connection

suspend fun <T> dbQuery(
    block: suspend () -> T,
): T = newSuspendedTransaction(transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ) { block() }