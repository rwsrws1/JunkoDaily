package com.example.learncompose.navigation

import androidx.annotation.StringRes
import com.example.learncompose.R
import com.example.learncompose.core.designsystem.icons.AppIcons

data class TopLevelNavItem(
    val selectIconId: Int,
    val unSelectIconId: Int,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)

val NOTE = TopLevelNavItem(
    selectIconId = AppIcons.noteFilled,
    unSelectIconId = AppIcons.note,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
)

val ROUTINE = TopLevelNavItem(
    selectIconId = AppIcons.routineFilled,
    unSelectIconId = AppIcons.routine,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
)

val SPEND = TopLevelNavItem(
    selectIconId = AppIcons.spendFilled,
    unSelectIconId = AppIcons.spend,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
)

val TOP_LEVEL_NAV_ITEMS = mapOf (
    NoteKey to NOTE,
    RoutineKey to ROUTINE,
    SpendKey to SPEND
)