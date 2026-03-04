package com.spatulox.wine.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spatulox.wine.domain.enum.HistoryType
import com.spatulox.wine.domain.model.History
import com.spatulox.wine.viewModels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel
) {
    val history by historyViewModel.filteredhistoryList.collectAsStateWithLifecycle()

    /*val historyTest by remember {
        mutableStateOf(
            listOf(
                History(
                    id = 1,
                    wineId = 1,
                    type = HistoryType.ADD,
                    date = System.currentTimeMillis(),
                    reason = "Ajout Château Margaux (75cl)"
                ),
                History(
                    id = 2,
                    wineId = 2,
                    type = HistoryType.WITHDRAW,
                    date = System.currentTimeMillis() - 86400000, // Hier
                    reason = "Retrait dégustation Romanée-Conti"
                ),
                History(
                    id = 3,
                    wineId = 3,
                    type = HistoryType.ADD,
                    date = System.currentTimeMillis() - 2 * 86400000, // Avant-hier
                    reason = null
                ),
                History(
                    id = 4,
                    wineId = 1,
                    type = HistoryType.WITHDRAW,
                    date = System.currentTimeMillis() - 7 * 86400000, // Semaine dernière
                    reason = "Cadeau anniversaire"
                ),
                History(
                    id = 5,
                    wineId = 4,
                    type = HistoryType.ADD,
                    date = System.currentTimeMillis() - 30 * 86400000, // Mois dernier
                    reason = "Achat Bordeaux Supérieur (x6)"
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
            history,
            key = { index, item -> item.id }
        ) { index, item ->
            HistoryItem(item = item)
        }
    }
}