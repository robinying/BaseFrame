package com.robin.baseframe.base.arch

/**
 * UI 状态基类 — 每个页面的 UiState 必须是一个 data class 实现此接口。
 * ViewModel 通过 StateFlow<UiState> 暴露单一不可变状态。
 */
interface UiState
