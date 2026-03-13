
package com.spatulox.wine.ui.screens.components

import android.widget.HorizontalScrollView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.xr.compose.testing.toDp
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.model.StockWithWine
import com.spatulox.wine.domain.model.Wine
import org.intellij.lang.annotations.JdkConstants

@Composable
fun BottleGrid(
    shelves: List<Shelf>,
    stock: Map<Position, StockWithWine>? = null, // Only to fill the placement when needed
    wines: Map<Int, Wine>? = null,
    modifier: Modifier = Modifier,
    bottleSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 8.dp,
    bottleSize: Dp = 40.dp,
    neckSize: Dp = 20.dp,
    staggerOffset: Dp = 22.dp,
    onPositionClick: (Position) -> Unit,
) {
    val maxCols = shelves.maxOfOrNull { it.col } ?: 6
    val listState = rememberLazyListState()
    var containerWidthPx by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier.fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                containerWidthPx = coordinates.size.width.toFloat()
            }
    ) {

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(
                space = bottleSpacing,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
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

                            val stockWithWine = stock?.get(pos)
                            val wine = stockWithWine?.wine
                            val color = if (wines!= null && wine != null) {
                                // "Visible wines"
                                if (wines.containsKey(wine.id)) {
                                    wine.color ?: MaterialTheme.colorScheme.primary
                                } else {
                                    (wine.color?.copy(alpha = 0.4f) ?: MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            // Rond VISIBLE
                            BottlePositionPreview(
                                color = color,
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


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(8.dp)
                .padding(horizontal = 16.dp)
        ) {
            if (containerWidthPx > 0) {
                val layoutInfo = listState.layoutInfo
                val visibleItemCount = layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)
                val itemWidthPx = containerWidthPx / visibleItemCount
                val currentScrollPosition = listState.firstVisibleItemIndex * itemWidthPx + listState.firstVisibleItemScrollOffset
                val totalContentWidth = (maxCols * itemWidthPx).coerceAtLeast(1f)
                val scrollFraction = currentScrollPosition / (totalContentWidth - containerWidthPx)

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(scrollFraction.coerceIn(0.1f, 1f))
                        .align(Alignment.CenterStart)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(2.dp)
                        )
                )
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