package com.spatulox.wine.ui.screens.shelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spatulox.wine.domain.model.Position
import com.spatulox.wine.domain.model.Stock
import com.spatulox.wine.domain.model.Wine
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel
import kotlinx.coroutines.launch

@Composable
fun ShelfScreen(
    stockViewModel: StockViewModel,
    wineViewModel: WineViewModel,
    shelfViewModel: ShelfViewModel,
    modifier: Modifier = Modifier,
    onPositionClick: (position: Position) -> Unit = { _ -> }
) {
    val stockState by stockViewModel.stockState.collectAsState()
    val winesPositionMap by wineViewModel.filteredWinesMap.collectAsState()

    var coroutine = rememberCoroutineScope()

    var positionClicked by remember { mutableStateOf<Position?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(3) { shelfIndex ->
            ShelfView(
                shelfNumber = shelfIndex + 1,
                stock = stockState,
                wines = winesPositionMap,
                onPositionClick = { position -> positionClicked = position }
            )
        }
    }

    positionClicked?.let { position ->
        OnBottlePositionClick(
            wineViewModel = wineViewModel,
            stockViewModel = stockViewModel,
            position = position,
            onPlaceWine = {position, wine, reason ->
                coroutine.launch { stockViewModel.insert(position, wine, reason) }
            },
            onWithdraw = {position, reason ->
                coroutine.launch { stockViewModel.withdraw(position, reason) }
            },
            onDeleteStock = {position ->
                coroutine.launch { stockViewModel.delete(position) }
            },
            onDismiss = { positionClicked = null }
        )
    }
}