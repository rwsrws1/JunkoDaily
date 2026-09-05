package com.example.learncompose.oldLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.feature.experiment.data.local.UserDataStore
import com.example.learncompose.feature.experiment.data.repo.AuthState
import com.example.learncompose.feature.experiment.data.repo.IAuthRepository
import com.example.learncompose.navigation.OldAppNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val dataStore: UserDataStore,
    val authRepository: IAuthRepository
) : ViewModel() {
    var startDestination by mutableStateOf<OldAppNavKey?>(null)
        private set
    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        viewModelScope.launch {
            val isAgree = dataStore.getIsAgreeTerms()
            startDestination = if (isAgree) {
                OldAppNavKey.Main
            } else {
                OldAppNavKey.Welcome
            }
        }

        viewModelScope.launch {
            authRepository.authState.collect { globalAuthState ->
                authState = globalAuthState
            }
        }
    }
}