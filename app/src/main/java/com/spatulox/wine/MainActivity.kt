package com.spatulox.wine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.spatulox.wine.data.db.DatabaseProvider
import com.spatulox.wine.data.db.TransactionProvider
import com.spatulox.wine.data.repository.HistoryRepositoryImpl
import com.spatulox.wine.data.repository.ShelfRepositoryImpl
import com.spatulox.wine.data.repository.StockRepositoryImpl
import com.spatulox.wine.data.repository.WineRepositoryImpl
import com.spatulox.wine.navigation.AppNavGraph
import com.spatulox.wine.ui.theme.WineTheme
import com.spatulox.wine.viewModels.HistoryViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

class MainActivity : ComponentActivity() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var wineViewModel: WineViewModel
    private lateinit var stockViewModel: StockViewModel
    private lateinit var shelfViewModel: ShelfViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val transactionProvider = TransactionProvider(db)

        val historyRepository = HistoryRepositoryImpl(db.historyDao())
        val wineRepository = WineRepositoryImpl(db.wineDao())
        val stockRepository = StockRepositoryImpl(db.stockDao(), db.historyDao(), transactionProvider)
        val shelfRepository = ShelfRepositoryImpl(db.shelfDao())

        historyViewModel = HistoryViewModel(historyRepository)
        wineViewModel = WineViewModel(wineRepository)
        stockViewModel = StockViewModel(stockRepository)
        shelfViewModel = ShelfViewModel(shelfRepository)

        enableEdgeToEdge()
        setContent {
            WineApp(
                wineViewModel = wineViewModel,
                historyViewModel = historyViewModel,
                stockViewModel = stockViewModel,
                shelfViewModel = shelfViewModel
            )
        }
    }
}

@Composable
fun WineApp(
    wineViewModel: WineViewModel,
    historyViewModel: HistoryViewModel,
    stockViewModel: StockViewModel,
    shelfViewModel: ShelfViewModel
) {
    WineTheme {
        AppNavGraph(
            wineViewModel = wineViewModel,
            historyViewModel = historyViewModel,
            stockViewModel = stockViewModel,
            shelfViewModel = shelfViewModel
        )
    }
}