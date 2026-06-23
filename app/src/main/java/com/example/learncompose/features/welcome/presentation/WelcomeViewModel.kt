package com.example.learncompose.features.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.features.home.presentation.HomeContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeContract.State(items = listOf("goods1", "goods2")))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<HomeContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun handleIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.ClickItem -> {
                viewModelScope.launch {
                    _sideEffect.send(HomeContract.SideEffect.NavigationToDetail(intent.id))
                }
            }
        }
    }
}