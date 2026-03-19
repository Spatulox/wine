package com.spatulox.wine.ui.screens.shelf


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.model.Compartment
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.model.StockWithWine
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.BottleGrid
import com.spatulox.wine.ui.screens.components.DragState

@Composable
fun CompartmentView(
    compartment: Compartment,
    shelves: List<Shelf>?,
    stock: Map<Position, StockWithWine>,
    wines: Map<Int, Wine>,
    isParentEditing: Boolean,
    rectBounds: SnapshotStateMap<Rect, Position>,
    positionBounds: SnapshotStateMap<Position, Rect>,
    draggedPosition: Position? = null,
    hoveredPosition: Position? = null,
    onPositionDragStart: (Position, DragState) -> Unit = {_, _ ->},
    onDragEnd: (Position) -> Unit,
    onDragCancel: () -> Unit,
    onPositionDragHover: (Position?) -> Unit = {},
    onPositionClick: (Position) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = compartment.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if(isParentEditing){
                    IconButton(
                        onClick = onEditClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier ${compartment.name}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            shelves?.let {
                BottleGrid(
                    shelves = shelves,
                    stock = stock,
                    wines = wines,
                    rectBounds = rectBounds,
                    positionBounds = positionBounds,
                    isDraggingEnabled = isParentEditing,
                    draggedPosition = draggedPosition,
                    hoveredPosition = hoveredPosition,
                    onPositionClick = onPositionClick,
                    onPositionDragStart = onPositionDragStart,
                    onPositionDragHover = onPositionDragHover,
                    onDragCancel = onDragCancel,
                    onDragEnd = onDragEnd,
                )
            }
        }
    }
}