package com.junko.junkodaily.feature.experiment.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junko.junkodaily.feature.experiment.data.repo.IAuthRepository
import com.junko.junkodaily.feature.experiment.main.navigation.ExperimentNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperimentModel @Inject constructor(
    private val repository: IAuthRepository,
) : ViewModel() {

    private val _currentKey = MutableStateFlow<ExperimentNavKey>(ExperimentNavKey.Experiment)
    val currentKey = _currentKey.asStateFlow()

    fun handleIntent(intent: ExperimentContract.Intent) {
        when (intent) {
            is ExperimentContract.Intent.Logout -> {
                viewModelScope.launch {
                    repository.logout()
                }
            }
            is ExperimentContract.Intent.ChangeCurrentKey -> {
                _currentKey.value = intent.key
            }
        }
    }
}