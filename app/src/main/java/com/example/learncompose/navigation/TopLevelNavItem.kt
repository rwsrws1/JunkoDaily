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

val ROUTINE = TopLevelNavItem(
    selectIconId = AppIcons.routineFilled,
    unSelectIconId = AppIcons.routine,
    iconTextId = R.string.routine,
    titleTextId = R.string.routine,
)

val CHART = TopLevelNavItem(
    selectIconId = AppIcons.chartFilled,
    unSelectIconId = AppIcons.chart,
    iconTextId = R.string.chart,
    titleTextId = R.string.chart,
)

val SPEND = TopLevelNavItem(
    selectIconId = AppIcons.spendFilled,
    unSelectIconId = AppIcons.spend,
    iconTextId = R.string.spend,
    titleTextId = R.string.spend,
)

val TOP_LEVEL_NAV_ITEMS = mapOf (
    RoutineKey to ROUTINE,
    ChartKey to CHART,
//    SpendKey to SPEND
)