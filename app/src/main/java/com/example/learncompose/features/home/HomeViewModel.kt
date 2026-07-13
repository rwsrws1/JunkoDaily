package com.example.learncompose.features.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.repository.IAuthRepository
import com.example.learncompose.features.home.navigation.HomeNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: IAuthRepository,
) : ViewModel() {
//    private val _sideEffect = Channel<HomeContract.SideEffect>()
//    val sideEffect = _sideEffect.receiveAsFlow()

    private val _currentKey = MutableStateFlow<HomeNavKey>(HomeNavKey.Greeting)
    val currentKey = _currentKey.asStateFlow()

    fun handleIntent(intent: HomeContract.Intent) {
        when (intent) {
//            is HomeContract.Intent.Login -> {
//                viewModelScope.launch {
//                    repository.logout()
//                    _sideEffect.send(HomeContract.SideEffect.NavigateToLogin)
//                }
//            }
            is HomeContract.Intent.UserInfo -> {
                viewModelScope.launch {
                    repository.logout()
                }
            }
            is HomeContract.Intent.ChangeCurrentKey -> {
                _currentKey.value = intent.key
            }
        }
    }
}