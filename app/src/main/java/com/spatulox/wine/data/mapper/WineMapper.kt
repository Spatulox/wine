package com.spatulox.wine.data.mapper

import com.spatulox.wine.data.db.entity.WineEntity
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.model.Wine

class WineMapper {


    fun toDomain(entity: WineEntity): Wine {
        return Wine(
            id = entity.id,
            name = entity.name,
            year = entity.year,
            format = WineFormat.valueOf(entity.format)
        )
    }

    fun toEntity(wine: Wine): WineEntity{
        return WineEntity(
            id = wine.id,
            name = wine.name,
            year = wine.year,
            format = wine.format.name
        )
    }
}