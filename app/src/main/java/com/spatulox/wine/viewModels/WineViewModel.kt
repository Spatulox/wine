package com.spatulox.wine.viewModels

import androidx.lifecycle.ViewModel
import com.spatulox.wine.data.repository.WineRepositoryImpl

open class WineViewModel(
    private val wineRepository: WineRepositoryImpl
) : ViewModel() {
}