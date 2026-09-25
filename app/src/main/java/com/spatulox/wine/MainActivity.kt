package com.spatulox.wine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

    private val db by lazy { DatabaseProvider.getDatabase(applicationContext) }
    private val transactionProvider by lazy { TransactionProvider(db) }
    private val wineRepository by lazy { WineRepositoryImpl(db.wineDao()) }
    private val stockRepository by lazy { StockRepositoryImpl(db.stockDao(), db.wineDao(), transactionProvider) }
    private val shelfRepository by lazy { ShelfRepositoryImpl(db.shelfDao()) }
    private val compartmentRepository by lazy {
        CompartmentRepositoryImpl(db.compartmentDao(), shelfRepository, stockRepository, transactionProvider)
    }

    // Created through the ViewModelProvider: they survive rotations and are cleared with the activity
    private val wineViewModel: WineViewModel by viewModels { factory { WineViewModel(wineRepository) } }
    private val stockViewModel: StockViewModel by viewModels { factory { StockViewModel(stockRepository) } }
    private val shelfViewModel: ShelfViewModel by viewModels { factory { ShelfViewModel(shelfRepository) } }
    private val compartmentViewModel: CompartmentViewModel by viewModels {
        factory { CompartmentViewModel(compartmentRepository, shelfRepository) }
    }

    private inline fun <reified VM : ViewModel> factory(noinline create: () -> VM) =
        viewModelFactory { initializer { create() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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