package com.robin.baseframe.base.arch

/**
 * UI 事件基类 — 用户操作（点击、输入、滑动等）通过 sealed interface 定义。
 * View 层单向发送，ViewModel 通过 onEvent(event: UiEvent) 统一处理。
 */
interface UiEvent
