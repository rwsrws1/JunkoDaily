package com.example.learncompose.feature.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.core.data.api.RoutineRepoApi
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repo: RoutineRepoApi
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<RoutineContract.UiState> = _selectedDate
        .flatMapLatest { centerDate ->
            // 为了保证滑动流畅，同时监听：前一天、当天、后一天 的数据 Flow
            val prevDate = centerDate.minusDays(1)
            val nextDate = centerDate.plusDays(1)

            combine(
                repo.getCardsWithLogsByDate(prevDate),
                repo.getCardsWithLogsByDate(centerDate),
                repo.getCardsWithLogsByDate(nextDate)
            ) { prevLogs, centerLogs, nextLogs ->
                // 将多天的数据聚合到一个 Map 中
                mapOf(
                    prevDate to prevLogs,
                    centerDate to centerLogs,
                    nextDate to nextLogs
                )
            }.map { mapData ->
                RoutineContract.UiState(
                    selectedDate = centerDate,
                    cardWithLogsMap = mapData,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RoutineContract.UiState(isLoading = true)
        )

    fun handleIntent(intent: RoutineContract.Intent) {
        when (intent) {
            is RoutineContract.Intent.SelectDate -> {
                _selectedDate.value = intent.date
            }

            is RoutineContract.Intent.InsertCard -> {
                viewModelScope.launch {
                    repo.insertCard(
                        RoutineCard(cardText = intent.cardText, cardColor = intent.cardColor, cardImage = intent.cardImage)
                    )
                }
            }

            is RoutineContract.Intent.DeleteCardById -> {
                viewModelScope.launch { repo.deleteCardById(intent.cardId) }
            }

            is RoutineContract.Intent.UpsertDailyLog -> {
                viewModelScope.launch {
                    repo.upsertDailyLog(
                        RoutineDailyLog(
                            cardId = intent.cardId,
                            recordDate = intent.recordDate,
                            isCompleted = intent.isCompleted,
                            completedAt = intent.completedAt,
                        )
                    )
                }
            }
        }
    }
}