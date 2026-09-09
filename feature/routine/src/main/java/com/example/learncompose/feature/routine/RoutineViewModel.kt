package com.example.learncompose.feature.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.core.data.api.RoutineRepoApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepoApi: RoutineRepoApi
) : ViewModel() {
    val today: LocalDate = LocalDate.now()
    val flows = (0 until 30).map { dayOffset ->
        val targetDate = today.minusDays(dayOffset.toLong())
        routineRepoApi.getCardsWithLogsByDate(targetDate)
            .map { RoutineContract.UiState(it) }
    }

    val uiState = combine(flows) { array ->
        array.toList()
    }.stateIn(
        scope = viewModelScope,
        initialValue = listOf(RoutineContract.UiState()),
        started = SharingStarted.WhileSubscribed(5000)
    )

//    val uiState  = routineRepoApi.getCardsWithLogsByDate(LocalDate.now()).map {
//        RoutineContract.UiState(it)
//    }.stateIn(
//        scope = viewModelScope,
//        initialValue = RoutineContract.UiState(),
//        started = SharingStarted.WhileSubscribed(5000),
//    )

    fun handleIntent(intent: RoutineContract.Intent) {
        when (intent) {
            is RoutineContract.Intent.InsertCard -> {
                viewModelScope.launch {
                    routineRepoApi.insertCard(intent.card)
                }
            }
            is RoutineContract.Intent.DeleteCardById -> {
                viewModelScope.launch {
                    routineRepoApi.deleteCardById(intent.cardId)
                }
            }
            is RoutineContract.Intent.UpsertDailyLog -> {
                viewModelScope.launch {
                    routineRepoApi.upsertDailyLog(intent.log)
                }
            }
        }
    }
}