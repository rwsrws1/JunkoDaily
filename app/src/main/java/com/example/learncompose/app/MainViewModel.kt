package com.example.learncompose.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.local.UserDataStore
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.data.repository.IAuthRepository
import com.example.learncompose.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val dataStore: UserDataStore,
    val authRepository: IAuthRepository
) : ViewModel() {
    var startDestination by mutableStateOf<Screen?>(null)
        private set
    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        viewModelScope.launch {
            val isAgree = dataStore.getIsAgreeTerms()
            if (isAgree) {
                val userINfo = dataStore.getUserInfo()
                startDestination = if (userINfo.userId.isNotBlank()) Screen.Home else Screen.Login("")
            } else {
                startDestination = Screen.Welcome
            }
        }

        viewModelScope.launch {
            authRepository.authState.collect { globalAuthState ->
                authState = globalAuthState
            }
        }
    }
}