package com.example.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbTransaction(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(context = Dispatchers.IO, statement = block)