package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    description: String,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(24.dp)
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.large,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector,
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
    }
}
