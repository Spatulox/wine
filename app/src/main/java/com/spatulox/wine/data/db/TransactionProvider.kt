package com.spatulox.wine.data.db

import androidx.room.withTransaction

class TransactionProvider(private val db: AppDatabase) {
    suspend fun <T> run(block: suspend () -> T): T = db.withTransaction(block)
}