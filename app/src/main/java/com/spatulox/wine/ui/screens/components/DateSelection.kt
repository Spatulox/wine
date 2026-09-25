package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = {
                if (availableYears?.isNotEmpty() == true) {
                    // Nearest previous year: works with duplicates or a year missing from the list
                    availableYears.filter { it < displayYear }.maxOrNull()?.let(onYearChange)
                } else {
                    onYearChange((displayYear - 1).coerceAtLeast(MIN_YEAR))
                }
            }
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Année précédente", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            text = "$displayYear",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = {
                if (availableYears?.isNotEmpty() == true) {
                    availableYears.filter { it > displayYear }.minOrNull()?.let(onYearChange)
                } else {
                    onYearChange((displayYear + 1).coerceAtMost(LocalDate.now().year))
                }
            }
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Année suivante", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private const val MIN_YEAR = 1900
