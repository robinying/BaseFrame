package com.robin.baseframe.base.arch

/**
 * UI 副作用基类 — 一次性事件（Toast、SnackBar、Navigation）。
 * 通过 Channel 发送，确保配置变更后不重复消费。
 */
interface UiEffect
