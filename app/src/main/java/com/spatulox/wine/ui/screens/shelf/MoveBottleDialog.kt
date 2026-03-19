package com.spatulox.wine.ui.screens.shelf

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.StockWithWine

@Composable
fun MoveBottleDialog(
    from: Position,
    to: Position,
    stockState: Map<Position, StockWithWine>,
    onMove: (Position, Position) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Déplacer ?") },
        text = {
            Text("Déplacer de ${from} → ${to} ?")
        },
        confirmButton = {
            TextButton(onClick = { onMove(from, to) }) { Text("Déplacer") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Annuler") }
        }
    )
}
