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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Shelf
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine
import kotlinx.coroutines.launch

@Composable
fun ShelfView(
    shelf: Shelf,
    stock: Map<Position, Stock>,
    wines: Map<Int, Wine>,
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
                // Nom compartiment
                Text(
                    text = shelf.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier ${shelf.name}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }


            repeat(shelf.rows) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(shelf.cols) { colIndex ->
                        val position = Position(
                            shelfId = shelf.id,
                            row = rowIndex + 1,
                            col = colIndex + 1
                        )

                        BottlePosition(
                            position = position,
                            stock = stock[position],
                            wine = stock[position]?.wineId?.let { wines[it] },
                            onClick = onPositionClick
                        )
                    }
                }
            }
        }
    }
}