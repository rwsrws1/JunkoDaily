package com.example.learncompose.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: IAuthRepository,
) : ViewModel() {
//    private val _sideEffect = Channel<HomeContract.SideEffect>()
//    val sideEffect = _sideEffect.receiveAsFlow()

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
        }
    }
}