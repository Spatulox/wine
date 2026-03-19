package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.spatulox.wine.domain.model.Position
import kotlinx.coroutines.coroutineScope

data class DragState(
    val sourcePos: Position? = null,
    val currentFingerPos: Offset? = null
)

fun Modifier.dragGesture(
    pos: Position,
    isEnabled: Boolean,
    onDragStart: (Position, DragState) -> Unit = { _, _ -> },
    onDragEnd: (Position) -> Unit,
    onDragCancel: () -> Unit,
    onDragHover: (Position, Offset) -> Unit = { _, _ -> }
) = pointerInput(isEnabled) {
    coroutineScope {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset -> onDragStart(pos, DragState(pos, offset)) },
            onDragEnd = { onDragEnd(pos) },
            onDragCancel = onDragCancel,
            onDrag = { change, dragAmount ->
                onDragHover(pos, change.position)
                change.consume()
            }
        )
    }
}