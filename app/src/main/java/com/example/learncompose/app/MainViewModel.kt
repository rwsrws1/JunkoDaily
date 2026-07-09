package com.example.learncompose.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.local.UserDataStore
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.data.repository.IAuthRepository
import com.example.learncompose.navigation.AppNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val dataStore: UserDataStore,
    val authRepository: IAuthRepository
) : ViewModel() {
    var startDestination by mutableStateOf<AppNavKey?>(null)
        private set
    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        viewModelScope.launch {
            val isAgree = dataStore.getIsAgreeTerms()
            startDestination = if (isAgree) {
                AppNavKey.Home
            } else {
                AppNavKey.Welcome
            }
        }

        viewModelScope.launch {
            authRepository.authState.collect { globalAuthState ->
                authState = globalAuthState
            }
        }
    }
}