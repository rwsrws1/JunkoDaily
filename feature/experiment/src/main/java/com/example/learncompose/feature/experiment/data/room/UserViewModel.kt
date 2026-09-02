package com.example.learncompose.feature.experiment.data.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository) : ViewModel() {

    // 将 Flow 转为 StateFlow，确保 UI 配置变更（如旋转屏幕）时数据不丢失
    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 界面不可见 5 秒后停止收集
            initialValue = emptyList()
        )

    fun addUser(name: String, age: Int) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insert(User(fullName = name, age = age))
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.delete(user)
        }
    }
}