package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.background
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
import com.spatulox.wine.domain.model.Shelf

@Composable
fun BottleGrid(
    shelves: List<Shelf>,
    modifier: Modifier = Modifier,
    bottleSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 8.dp,
    bottleSize: Dp = 32.dp,
    neckSize: Dp = 20.dp,
    staggerOffset: Dp = 22.dp
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

                            // Rond VISIBLE
                            BottlePositionPreview(
                                arrangement = shelf.arrangement,
                                offsetX = offset,
                                bottleSize = bottleSize,
                                neckSize = neckSize
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
    arrangement: BottlePosition,
    offsetX: Dp,
    bottleSize: Dp,
    neckSize: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(bottleSize)
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
                    MaterialTheme.colorScheme.primary,
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