package com.spatulox.wine.data.repository

import com.spatulox.wine.data.db.dao.WineDao
import com.spatulox.wine.domain.repository.WineRepository

class WineRepositoryImpl(private val wineDao: WineDao): WineRepository {
}