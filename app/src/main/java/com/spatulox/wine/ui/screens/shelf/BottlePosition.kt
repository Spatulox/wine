package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine

@Composable
fun BottlePosition(
    position: Position,
    stock: Stock?,
    wine: Wine?,
    onClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOccupied = stock != null

    OutlinedButton(
        onClick = { onClick(position) },
        modifier = modifier
            .size(50.dp)
            .padding(2.dp),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isOccupied) MaterialTheme.colorScheme.primaryContainer
            else Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isOccupied) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isOccupied && wine != null) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = wine.name,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color.White,
                        CircleShape
                    )
            )
        }
    }
}