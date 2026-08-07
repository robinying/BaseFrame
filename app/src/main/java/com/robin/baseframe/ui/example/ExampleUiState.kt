package com.robin.baseframe.ui.example

import com.robin.baseframe.base.arch.UiEffect
import com.robin.baseframe.base.arch.UiEvent
import com.robin.baseframe.base.arch.UiState
import com.robin.baseframe.domain.model.HomeData

/**
 * 示例页面 UiState — 页面单一不可变状态。
 */
data class ExampleUiState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null
) : UiState

/**
 * 示例页面 UiEvent — 用户操作事件。
 */
sealed interface ExampleUiEvent : UiEvent {
    object LoadHome : ExampleUiEvent
    object Refresh : ExampleUiEvent
}

/**
 * 示例页面 UiEffect — 一次性副作用。
 */
sealed interface ExampleUiEffect : UiEffect {
    data class ShowToast(val message: String) : ExampleUiEffect
    data class ShowError(val message: String) : ExampleUiEffect
}
