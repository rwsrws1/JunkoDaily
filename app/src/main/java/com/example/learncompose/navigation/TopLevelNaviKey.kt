package com.example.learncompose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object NoteKey : NavKey
@Serializable
data object RoutineKey : NavKey
@Serializable
data object SpendKey : NavKey
@Serializable
data object NoteDetailKey : NavKey