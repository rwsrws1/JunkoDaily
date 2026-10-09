package com.junko.junkodaily.feature.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junko.junkodaily.core.data.repository.OfflineRoutineRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repo: OfflineRoutineRepo,
) : ViewModel() {
    val uiState: StateFlow<ChartContract.UiState> = repo.getCardsWithLogs()
        .map { cardsAndLogs ->
            ChartContract.UiState(
                isLoading = false,
                cardsAndLogs = cardsAndLogs
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChartContract.UiState(isLoading = true)
        )
}