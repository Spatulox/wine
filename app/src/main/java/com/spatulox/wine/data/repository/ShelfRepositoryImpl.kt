package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.ShelfDao
import com.spatulox.wine.domain.repository.ShelfRepository

class ShelfRepositoryImpl(private val shelfDao: ShelfDao): ShelfRepository {
}