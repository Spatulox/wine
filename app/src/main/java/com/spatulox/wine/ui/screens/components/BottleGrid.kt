
package com.spatulox.wine.ui.screens.components

import android.widget.HorizontalScrollView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.spatulox.wine.domain.enum.BottlePosition
import com.spatulox.wine.domain.enum.ShelfInterleave
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.model.StockWithWine
import com.spatulox.wine.domain.model.Wine
import kotlin.math.hypot

@Composable
fun BottleGrid(
    shelves: List<Shelf>,
    stock: Map<Position, StockWithWine>? = null,
    wines: Map<Int, Wine>? = null,
    modifier: Modifier = Modifier,
    bottleSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 8.dp,
    bottleSize: Dp = 40.dp,
    neckSize: Dp = 20.dp,
    staggerOffset: Dp = 26.dp,
    positionBounds: SnapshotStateMap<Position, Rect>? = null,
    isDraggingEnabled: Boolean = false,
    draggedPosition: Position? = null,
    hoveredPosition: Position? = null,
    onFingerPositionUpdate: (Offset) -> Unit? = {},
    onPositionDragStart: (Position, DragState) -> Unit = { _, _ -> },
    onPositionDragHover: (Position?) -> Unit = {},
    onDragEnd: (Position) -> Unit,
    onDragCancel: () -> Unit,
    onPositionClick: (Position) -> Unit,
) {
    val maxCols = shelves.maxOfOrNull { it.col } ?: 6
    val listState = rememberLazyListState()

    val density = LocalDensity.current

    // HIT DETECTION with tolerance bottleSize/2
    fun findTargetPosition(fingerPos: Offset): Position? {
        val tolerancePx = with(density) { (bottleSize / 2 + bottleSpacing / 2).toPx() }

        return positionBounds?.entries
            ?.mapNotNull { (pos, bounds) ->
                val distance = hypot(
                    fingerPos.x - bounds.center.x,
                    fingerPos.y - bounds.center.y
                )
                if (distance <= tolerancePx) pos to distance else null
            }
            ?.minByOrNull { it.second }?.first
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(bottleSpacing, Alignment.CenterHorizontally),
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

                            if (positionBounds != null) {
                                // A position leaving the composition (scrolled away, deleted shelf...)
                                // must not stay a drop target at its last known place
                                DisposableEffect(pos) {
                                    onDispose { positionBounds.remove(pos) }
                                }
                            }

                            val stockWithWine = stock?.get(pos)
                            val isEmpty = stock != null && stockWithWine == null
                            val color = when {
                                // Shelf preview (no stock at all): a color per position
                                stock == null -> PREVIEW_COLORS[(pos.col + pos.shelf * 3 + pos.compartment) % PREVIEW_COLORS.size]
                                // Empty spot: drawn as an outline only
                                stockWithWine == null -> MaterialTheme.colorScheme.outline
                                // Bottle matching the current filter
                                wines?.containsKey(stockWithWine.wine.id) == true ->
                                    wines[stockWithWine.wine.id]?.color ?: MaterialTheme.colorScheme.primary
                                // Bottle hidden by the current filter
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                            }

                            BottlePositionPreview(
                                color = color,
                                isEmpty = isEmpty,
                                arrangement = shelf.arrangement,
                                offsetX = offset,
                                bottleSize = bottleSize,
                                neckSize = neckSize,
                                isDragging = draggedPosition == pos && isDraggingEnabled,
                                positionBounds = { bounds ->
                                    positionBounds?.let { positionBounds[pos] = bounds }
                                },
                                modifier = Modifier
                                    .dragGesture(
                                        pos = pos,
                                        isEnabled = isDraggingEnabled,
                                        onDragStart = onPositionDragStart,
                                        onDragHover = { initPosition, offset -> // Pos + relative offset
                                            if(positionBounds == null) {
                                                return@dragGesture
                                            }
                                            val initBounds = positionBounds[initPosition] ?: return@dragGesture

                                            // Create the absolute offset
                                            val fingerPosAbsolu = Offset(
                                                x = initBounds.topLeft.x + offset.x,
                                                y = initBounds.topLeft.y + offset.y
                                            )

                                            onFingerPositionUpdate(fingerPosAbsolu)
                                            val targetPos = findTargetPosition(fingerPosAbsolu) // Find the target with a error margin

                                            if(targetPos != null) {
                                                onPositionDragHover(targetPos)
                                            } else {
                                                onPositionDragHover(null)
                                            }
                                        },
                                        onDragCancel = onDragCancel,
                                        onDragEnd = onDragEnd
                                    ),
                                onClick = { onPositionClick(pos) }
                            )
                        } else {
                            InvisibleBottle(bottleSize)
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
    isEmpty: Boolean = false,
    arrangement: BottlePosition,
    offsetX: Dp,
    bottleSize: Dp,
    neckSize: Dp,
    isDragging: Boolean = false,
    positionBounds: (Rect) -> Unit = {},
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            // Gestures after the offset: the touch area and the drag coordinates match the drawn
            // bottle and its reported bounds
            .then(modifier)
            .size(bottleSize)
            .onGloballyPositioned { coords ->
                val newBounds = Rect(
                    offset = coords.positionInRoot(),
                    size = coords.size.toSize()
                )
                positionBounds(newBounds)
            }
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
                .scale(if (isDragging) 1.1f else 1f)
                .alpha(if (isDragging) 0.7f else 1f)
                .then(
                    if (isEmpty) Modifier.border(2.dp, color, CircleShape)
                    else Modifier.background(color, CircleShape)
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

private val PREVIEW_COLORS = listOf(
    Color(0xFFFF6B6B), // Coral
    Color(0xFF4ECDC4), // Turquoise
    Color(0xFF45B7D1), // Sky blue
    Color(0xFF96CEB4), // Mint
    Color(0xFFFFEEAD), // Pale yellow
    Color(0xFFD4A5A5), // Light pink
    Color(0xFF9B59B6), // Amethyst
    Color(0xFF3498DB), // Blue
    Color(0xFFE74C3C), // Red
    Color(0xFF2ECC71)  // Emerald
)
