
package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.model.StockWithWine

@Composable
fun BottleGrid(
    shelves: List<Shelf>,
    stock: Map<Position, StockWithWine>? = null, // Only to fill the placement when needed
    modifier: Modifier = Modifier,
    bottleSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 8.dp,
    bottleSize: Dp = 32.dp,
    neckSize: Dp = 20.dp,
    staggerOffset: Dp = 22.dp,
    onPositionClick: (Position) -> Unit,
) {
    val maxCols = shelves.maxOfOrNull { it.col } ?: 6

    Box(
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(bottleSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            items(maxCols) { colIndex ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    shelves.forEach { shelf ->
                        if (colIndex < shelf.col) {
                            val offset = when (shelf.aligment) {
                                ShelfInterleave.STRAIGHT -> 0.dp
                                ShelfInterleave.STAGGERED_LEFT -> (-staggerOffset)
                                ShelfInterleave.STAGGERED_RIGHT -> staggerOffset
                            }

                            val pos = Position(
                                compartment = shelf.compartmentId,
                                shelf = shelf.id,
                                col = colIndex
                            )

                            // Rond VISIBLE
                            BottlePositionPreview(
                                color = stock?.get(pos)?.wine?.color ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                arrangement = shelf.arrangement,
                                offsetX = offset,
                                bottleSize = bottleSize,
                                neckSize = neckSize,
                                onClick = {
                                    onPositionClick(pos)
                                }
                            )
                        } else {
                            // Rond INVISIBLE pour aligner
                            InvisibleBottle(
                                size = bottleSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottlePositionPreview(
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    arrangement: BottlePosition,
    offsetX: Dp,
    bottleSize: Dp,
    neckSize: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(bottleSize)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(
                    when (arrangement) {
                        BottlePosition.BASE -> bottleSize
                        BottlePosition.NECK -> neckSize
                    }
                )
                .align(Alignment.Center)
                .background(
                    color,
                    CircleShape
                )
        )
    }
}
@Composable
private fun InvisibleBottle(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color.Transparent, CircleShape)
    )
}