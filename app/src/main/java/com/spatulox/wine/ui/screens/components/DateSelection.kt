package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DateSelection(
    year: Int?,
    onYearChange: (Int) -> Unit,
    availableYears: List<Int>? = null,
    modifier: Modifier = Modifier
) {

    val displayYear = when {
        year != null -> year
        availableYears?.isNotEmpty() == true -> availableYears.last()
        else -> LocalDate.now().year - 3
    }
    onYearChange(displayYear)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = {
                if (availableYears?.isNotEmpty() == true) {
                    val currentIndex = availableYears.indexOf(year)
                    if (currentIndex > 0) {
                        onYearChange( (availableYears[currentIndex - 1]) )
                    }
                } else {
                    onYearChange((displayYear - 1))
                }
            }
        ) {
            Icon(Icons.Filled.KeyboardArrowLeft, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            text = "${year}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = {
                if (availableYears?.isNotEmpty() == true) {
                    val currentIndex = availableYears.indexOf(year)
                    if (currentIndex < availableYears.lastIndex) {
                        onYearChange(availableYears[currentIndex + 1])
                    }
                } else {
                    onYearChange((displayYear + 1).coerceAtMost(LocalDate.now().year))
                }
            }
        ) {
            Icon(Icons.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
