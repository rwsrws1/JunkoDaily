package com.example.learncompose.feature.routine.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.learncompose.core.data.repository.OfflineRoutineRepo
import com.example.learncompose.core.model.RoutineCardsAndLogs
import com.example.learncompose.core.model.RoutineDailyLog
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RoutineChartViewModel @Inject constructor(
    private val repo: OfflineRoutineRepo,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = repo.getAllCards()
        .flatMapLatest { cards ->
            if (cards.isEmpty()) {
                // 如果卡片列表为空，直接返回包含空列表的 Flow
                flowOf(emptyList())
            } else {
                // 1. 为每个 cardId 创建获取 DailyLog 的 Flow
                val logFlows: List<Flow<RoutineCardsAndLogs>> = cards.map { card ->
                    repo.getCardsAllDailyLog(card.id).map { logs ->
                        RoutineCardsAndLogs(card = card, logs = logs)
                    }
                }
                // 2. 将所有卡片的 Flow 组合为一个并发射最新的 List<CardWithLogs>
                combine(logFlows) { array ->
                    array.toList()
                }
            }
        }.map {
            RoutineChartContract.UiState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RoutineChartContract.UiState()
        )

}