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
import com.spatulox.wine.data.repository.CompartmentRepositoryImpl
import com.spatulox.wine.data.repository.ShelfRepositoryImpl
import com.spatulox.wine.data.repository.StockRepositoryImpl
import com.spatulox.wine.data.repository.WineRepositoryImpl
import com.spatulox.wine.navigation.AppNavGraph
import com.spatulox.wine.ui.theme.WineTheme
import com.spatulox.wine.viewModels.CompartmentViewModel
import com.spatulox.wine.viewModels.ShelfViewModel
import com.spatulox.wine.viewModels.StockViewModel
import com.spatulox.wine.viewModels.WineViewModel

class MainActivity : ComponentActivity() {

    private lateinit var wineViewModel: WineViewModel
    private lateinit var stockViewModel: StockViewModel
    private lateinit var shelfViewModel: ShelfViewModel
    private lateinit var compartmentViewModel: CompartmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val transactionProvider = TransactionProvider(db)

        val wineRepository = WineRepositoryImpl(db.wineDao())
        val stockRepository = StockRepositoryImpl(db.stockDao(), transactionProvider)
        val shelfRepository = ShelfRepositoryImpl(db.shelfDao())
        val compartmentRepository = CompartmentRepositoryImpl(db.compartmentDao(), shelfRepository,  transactionProvider)

        wineViewModel = WineViewModel(wineRepository)
        stockViewModel = StockViewModel(stockRepository)
        shelfViewModel = ShelfViewModel(shelfRepository)
        compartmentViewModel = CompartmentViewModel(compartmentRepository, shelfRepository)

        enableEdgeToEdge()
        setContent {
            WineApp(
                wineViewModel = wineViewModel,
                stockViewModel = stockViewModel,
                shelfViewModel = shelfViewModel,
                compartmentViewModel = compartmentViewModel
            )
        }
    }
}

@Composable
fun WineApp(
    wineViewModel: WineViewModel,
    stockViewModel: StockViewModel,
    shelfViewModel: ShelfViewModel,
    compartmentViewModel: CompartmentViewModel
) {
    WineTheme {
        AppNavGraph(
            wineViewModel = wineViewModel,
            stockViewModel = stockViewModel,
            shelfViewModel = shelfViewModel,
            compartmentViewModel = compartmentViewModel
        )
    }
}