
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.xr.compose.testing.toDp
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
    staggerOffset: Dp = 22.dp,
    rectBounds: SnapshotStateMap<Rect, Position>? = null,
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

    // HIT DETECTION MAP : Position -> Rect (bounds à l'écran)
    //val rectBounds = remember { mutableStateMapOf<Rect, Position>() }
    //val positionBounds = remember { mutableStateMapOf<Position, Rect>() }
    //var currentDragFingerPos by remember { mutableStateOf<Offset?>(null) }

    val density = LocalDensity.current

    // HIT DETECTION avec tolérance bottleSize/2
    fun findTargetPosition(fingerPos: Offset): Position? {
        val tolerancePx = with(density) { (bottleSize / 2 + bottleSpacing / 2).toPx() }

        return rectBounds?.entries
            ?.mapNotNull { (bounds, pos) ->
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

                            val stockWithWine = stock?.get(pos)
                            val wine = stockWithWine?.wine
                            val color = if (wines != null && wine != null) {
                                if (wines.containsKey(wine.id)) {
                                    wine.color ?: MaterialTheme.colorScheme.primary
                                } else {
                                    (wine.color?.copy(alpha = 0.4f) ?: MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            BottlePositionPreview(
                                color = color,
                                arrangement = shelf.arrangement,
                                offsetX = offset,
                                bottleSize = bottleSize,
                                neckSize = neckSize,
                                isDragging = draggedPosition == pos && isDraggingEnabled,
                                positionBounds = { bounds ->
                                    rectBounds?.let { rectBounds[bounds] = pos }
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

                                            /*println(rectBounds)
                                            println("initPosition: $initPosition / offset: $offset / doigt=$fingerPosAbsolu")
                                            println("${Position(1,1, 0)} ${positionBounds[Position(1,1, 0)]}")
                                            println("${Position(1,1, 1)} ${positionBounds[Position(1,1, 1)]}")
                                            println("${targetPos}  ${positionBounds[targetPos]}")*/
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
    arrangement: BottlePosition,
    offsetX: Dp,
    bottleSize: Dp,
    neckSize: Dp,
    isDragging: Boolean = false,
    positionBounds: (Rect) -> Unit = {}, // ← NOUVEAU : callback pour les bounds
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var localBounds by remember { mutableStateOf<Rect?>(null) }


    Box(
        modifier = modifier
            .offset(x = offsetX)
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
                .background(color, CircleShape)
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