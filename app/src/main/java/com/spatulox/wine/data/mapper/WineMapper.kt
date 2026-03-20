package com.spatulox.wine.data.mapper

import androidx.compose.ui.graphics.Color
import com.spatulox.wine.data.db.entity.WineEntity
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.enum.WineRegion
import com.spatulox.wine.domain.enum.WineType
import com.spatulox.wine.domain.model.Wine

object WineMapper {

    private fun Color.toHex(): String {
        val colorInt = android.graphics.Color.argb(
            (this.alpha * 255).toInt(),
            (this.red * 255).toInt(),
            (this.green * 255).toInt(),
            (this.blue * 255).toInt()
        )
        return String.format("#%08X", colorInt)
    }

    private fun String.toColor(): Color? = try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        null
    }
    fun toDomain(entity: WineEntity): Wine {
        return Wine(
            id = entity.id,
            name = entity.name,
            year = entity.year,
            format = WineFormat.valueOf(entity.format),
            type = WineType.valueOf(entity.type),
            unitPrice = entity.unitPrice,
            stars = entity.stars,
            qte = entity.qte,
            region = entity.region?.let { WineRegion.valueOf(it) },
            color = entity.color?.toColor()
        )
    }

    fun toEntity(wine: Wine): WineEntity{
        return WineEntity(
            id = wine.id,
            name = wine.name.trim(),
            year = wine.year,
            format = wine.format.name,
            type = wine.type.name,
            unitPrice = wine.unitPrice,
            stars = wine.stars,
            qte = wine.qte,
            region = wine.region?.name,
            color = wine.color?.toHex()
        )
    }
}