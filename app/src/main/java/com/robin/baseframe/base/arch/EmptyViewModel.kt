package com.robin.baseframe.base.arch

import com.robin.baseframe.app.base.BaseViewModel

/**
 * 空白 UiState — 用于不需要状态管理的简单页面。
 * 过渡期使用，后续迁移到具体 data class。
 */
data class EmptyUiState(
    val __placeholder: Boolean = false
) : UiState

/**
 * 空白 UiEvent — 用于没有用户事件的简单页面。
 */
sealed interface EmptyUiEvent : UiEvent {
    object NoOp : EmptyUiEvent
}

/**
 * 空白 UiEffect — 用于没有副作用的简单页面。
 */
sealed interface EmptyUiEffect : UiEffect {
    data class ShowToast(val message: String) : EmptyUiEffect
}

/**
 * 空 ViewModel — 过渡期用，新页面应创建具体的 UiState/UiEvent/UiEffect。
 * 不标记 @HiltViewModel，子类使用 Hilt 注入时手动标记。
 */
open class EmptyViewModel : BaseViewModel<EmptyUiState, EmptyUiEvent, EmptyUiEffect>(EmptyUiState()) {

    override fun onEvent(event: EmptyUiEvent) {
        // no-op
    }

    fun showToast(message: String) {
        sendEffect(EmptyUiEffect.ShowToast(message))
    }
}
