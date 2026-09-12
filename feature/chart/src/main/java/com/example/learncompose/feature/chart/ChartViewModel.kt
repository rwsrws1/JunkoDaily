package com.example.learncompose.feature.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.core.data.repository.OfflineRoutineRepo
import com.example.learncompose.core.model.RoutineCardsAndLogs
import com.example.learncompose.feature.routine.chart.ChartContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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