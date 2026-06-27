package com.example.learncompose.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.local.UserDataStore
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.data.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val repository: IAuthRepository,
) : ViewModel() {
    private val _sideEffect = Channel<HomeContract.SideEffect>()
    var sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.authState.collect { authState ->
                if (authState == AuthState.LoggedOut) {
                    _sideEffect.send(HomeContract.SideEffect.NavigateToLogin(""))
                }
            }
        }
    }

    fun handleIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.Logout -> {
                viewModelScope.launch {
                    repository.logout()
                }
            }
        }
    }
}