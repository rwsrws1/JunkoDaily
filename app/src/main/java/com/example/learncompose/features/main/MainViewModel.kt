package com.example.learncompose.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learncompose.data.repo.IAuthRepository
import com.example.learncompose.features.main.navigation.MainNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: IAuthRepository,
) : ViewModel() {

    private val _currentKey = MutableStateFlow<MainNavKey>(MainNavKey.Home)
    val currentKey = _currentKey.asStateFlow()

    fun handleIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.Logout -> {
                viewModelScope.launch {
                    repository.logout()
                }
            }
            is MainContract.Intent.ChangeCurrentKey -> {
                _currentKey.value = intent.key
            }
        }
    }
}