package com.example.learncompose.features.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.features.home.presentation.HomeContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WelcomeContract.State(items = listOf("goods1", "goods2")))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<WelcomeContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun handleIntent(intent: WelcomeContract.Intent) {
        when (intent) {
            is WelcomeContract.Intent.ClickItem -> {
                viewModelScope.launch {
                    _sideEffect.send(WelcomeContract.SideEffect.NavigateToLogin(intent.id))
                }
            }
            is WelcomeContract.Intent.PlusItem -> {
                _uiState.update {
                    it.copy(testNumber = it.testNumber + 1)
                }
            }
        }
    }
}