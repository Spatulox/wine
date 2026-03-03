package com.spatulox.wine.ui.screens.wine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.WineFormat
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.ui.screens.components.Filter
import com.spatulox.wine.ui.screens.components.SearchWithFilters
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.launch

@Composable
fun WineScreen(
    wineViewModel: WineViewModel
) {
    val wines by wineViewModel.wines.collectAsStateWithLifecycle()

    var selectedWine by remember { mutableStateOf<Wine?>(null) }

    val coroutineScope = rememberCoroutineScope()

    selectedWine?.let { wine ->
        WineEditDialog(
            wine = wine,
            onDismiss = { selectedWine = null },
            onValidate = { updatedWine ->
                coroutineScope.launch {  // ← COROUTINE
                    wineViewModel.updateWine(updatedWine)
                }
                //wineViewModel.updateWine(updatedWine)
                selectedWine = null
            },
            onDelete = {
                coroutineScope.launch {  // ← COROUTINE
                    wineViewModel.deleteWine(wine)
                }
                //wineViewModel.deleteWine(wine)
                selectedWine = null
            }
        )
    }

    /*val winesTest by remember {
        mutableStateOf(
            mapOf(
                1 to Wine(
                    id = 1,
                    name = "Château Margaux",
                    year = 2018,
                    format = WineFormat.BOTTLE,
                    stars = 5
                ),
                2 to Wine(
                    id = 2,
                    name = "Domaine Romanée-Conti",
                    year = 2015,
                    format = WineFormat.MAGNUM,
                    stars = 4
                ),
                3 to Wine(
                    id = 3,
                    name = "Pétrus",
                    year = 2020,
                    format = WineFormat.BOTTLE,
                    stars = 5
                ),
                4 to Wine(
                    id = 4,
                    name = "Bordeaux Supérieur",
                    year = 2022,
                    format = WineFormat.BOTTLE,
                    stars = 3
                )
            )
        )
    }*/

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            wines.values.toList(),
            key = { index, wine -> wine.id }
        ) { index, wine ->
            WineItem(
                wine = wine,
                onClick = { selectedWine = wine }
            )
        }
    }

}