package com.example.learncompose.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.learncompose.data.repository.UserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class UserDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val IS_AGREE_TERMS = booleanPreferencesKey("is_agree_terms")
        val TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userTokenFLow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
        .map { preferences ->
            preferences[PreferencesKeys.TOKEN] ?: ""
        }

    val userInfoFlow: Flow<UserInfo> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            UserInfo(
                userId = preferences[PreferencesKeys.USER_ID] ?: "",
                userName = preferences[PreferencesKeys.USER_NAME] ?: ""
            )
        }

    suspend fun getUserInfo(): UserInfo {
        val preferences = dataStore.data.catch { emit(emptyPreferences()) }.first()
        return UserInfo(
            userId = preferences[PreferencesKeys.USER_ID] ?: "",
            userName = preferences[PreferencesKeys.USER_NAME] ?: ""
        )
    }

    suspend fun saveUserSession(token: String, userInfo: UserInfo) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
            preferences[PreferencesKeys.USER_ID] = userInfo.userId
            preferences[PreferencesKeys.USER_NAME] = userInfo.userName
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_NAME)
        }
    }

    suspend fun agreeTerms() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AGREE_TERMS] = true
        }
    }

    suspend fun getIsAgreeTerms(): Boolean {
        delay(1000.milliseconds)
        val preferences = dataStore.data.catch { emptyPreferences() }.first()
        return preferences[PreferencesKeys.IS_AGREE_TERMS] ?: false
    }
}