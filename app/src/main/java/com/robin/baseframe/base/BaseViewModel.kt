package com.robin.baseframe.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robin.baseframe.base.arch.UiEffect
import com.robin.baseframe.base.arch.UiEvent
import com.robin.baseframe.base.arch.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVVM + UDF 基类 ViewModel。
 *
 * S: UiState  — 页面的单一不可变状态
 * E: UiEvent  — 用户操作事件
 * F: UiEffect — 一次性副作用（Toast、导航等）
 *
 * 使用方式：
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     private val myUseCase: MyUseCase
 * ) : BaseViewModel<MyUiState, MyUiEvent, MyUiEffect>(MyUiState()) {
 *     override fun onEvent(event: MyUiEvent) { ... }
 * }
 * ```
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effect = Channel<F>(Channel.BUFFERED)
    val effect: Flow<F> = _effect.receiveAsFlow()

    /**
     * 单一事件入口 — View 层只通过此方法发送用户操作。
     */
    abstract fun onEvent(event: E)

    /**
     * 原子性更新 UI 状态。
     * 使用 data class 的 copy 机制保证不可变性。
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    /**
     * 发送一次性副作用事件。
     */
    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _effect.send(effect) }
    }

    /**
     * 在主线程执行协程。
     */
    protected fun launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) { block() }
    }

    /**
     * 在 IO 线程执行协程。
     */
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) { block() }
    }
}
