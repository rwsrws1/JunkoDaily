package com.example.learncompose.data.repository

import android.net.Credentials
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.learncompose.data.local.UserDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AuthState {
    object Loading : AuthState
    object LoggedOut : AuthState
    data class LoggedIn(val userInfo: UserInfo) : AuthState
}

data class UserInfo(val userId: String, val userName: String)

interface IAuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun login(account: String, password: String)
    suspend fun logout()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore
) : IAuthRepository {
    override val authState: StateFlow<AuthState> = dataStore.userTokenFLow
        .combine(dataStore.userInfoFlow) { token, userINfo ->
            if (token.isEmpty()) {
                AuthState.LoggedOut
            } else {
                AuthState.LoggedIn(userInfo = userINfo)
            }
        }
        .stateIn(
            scope = ProcessLifecycleOwner.get().lifecycleScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    override suspend fun login(account: String, password: String) {
        val mockToken = "jwt_token_123456"
        val mockUser = UserInfo("9527", "开发者小明")
        dataStore.saveUserSession(mockToken, mockUser)
    }

    override suspend fun logout() {
        dataStore.clearUserSession()
    }
}