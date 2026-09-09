package com.example.learncompose.feature.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.core.data.api.RoutineRepoApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepoApi: RoutineRepoApi
) : ViewModel() {
    val uiState  = routineRepoApi.getRoutineCard().map {
        RoutineContract.UiState(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = RoutineContract.UiState(),
        started = SharingStarted.WhileSubscribed(5000),
    )

    fun handleIntent(intent: RoutineContract.Intent) {
        when (intent) {
            is RoutineContract.Intent.InsertRoutineCard -> {
                viewModelScope.launch {
                    routineRepoApi.insertRoutineCard(routineCard = intent.routineCard)
                }
            }
            is RoutineContract.Intent.DeleteRoutineCard -> {
                viewModelScope.launch {
                    routineRepoApi.deleteRoutineCard(routineCard = intent.routineCard)
                }
            }
            is RoutineContract.Intent.UpdateRoutineCard -> {
                viewModelScope.launch {
                    routineRepoApi.updateRoutineCard(routineCard = intent.routineCard)
                }
            }
        }
    }
}