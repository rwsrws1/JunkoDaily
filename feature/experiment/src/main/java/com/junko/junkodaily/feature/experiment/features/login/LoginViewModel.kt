package com.junko.junkodaily.feature.experiment.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class LoginViewModel @Inject constructor(
    private val repository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginContract.State())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<LoginContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.authState.collect { globalAuthState ->
                _uiState.update { it.copy(authState = globalAuthState) }

                if (globalAuthState is AuthState.LoggedIn) {
                    _sideEffect.send(LoginContract.SideEffect.NavigateToMain)
                }
            }
        }
    }

    fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.ClickLogin -> {
                _uiState.update { it.copy(authState = AuthState.Loading) }
                viewModelScope.launch {
                    repository.login(intent.account, intent.password)
                }
            }
        }
    }
}