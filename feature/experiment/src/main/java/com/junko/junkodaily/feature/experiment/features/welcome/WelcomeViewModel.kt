package com.junko.junkodaily.feature.experiment.features.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junko.junkodaily.feature.experiment.data.local.UserDataStore
import com.junko.junkodaily.feature.experiment.data.repo.AuthState
import com.junko.junkodaily.feature.experiment.data.repo.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val dataStore: UserDataStore,
    private val repository: IAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WelcomeContract.State(items = listOf("goods1", "goods2")))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<WelcomeContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.authState.collect { globalAuthState ->
                _uiState.update { it.copy(authState = globalAuthState) }

                if (globalAuthState is AuthState.LoggedIn) {
                    _sideEffect.send(WelcomeContract.SideEffect.LoginAsVisitor())
                }
            }
        }
    }

    fun handleIntent(intent: WelcomeContract.Intent.ViewModelIntent) {
        when (intent) {
            is WelcomeContract.Intent.ClickEnter -> {
                viewModelScope.launch {
                    dataStore.agreeTerms()
                    repository.login("rws991123@gmail.com", "123456")
                }
            }
            is WelcomeContract.Intent.PlusItem -> {
                _uiState.update {
                    it.copy(testNumber =
                        if (it.testNumber < 10086) {
                            it.testNumber + 1
                        } else {
                            it.testNumber
                        }
                    )
                }
            }
        }
    }
}