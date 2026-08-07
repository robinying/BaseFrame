package com.robin.baseframe.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robin.baseframe.base.arch.UiEffect
import com.robin.baseframe.base.arch.UiEvent
import com.robin.baseframe.base.arch.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Compose 页面的 MVVM+UDF 基类。
 *
 * 子类在 [onCreate] 中调用 [setMVVMContent] 即可获得完整的状态订阅和事件转发。
 *
 * 使用方式：
 * ```kotlin
 * @AndroidEntryPoint
 * class ExampleActivity : BaseComposeActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setMVVMContent<ExampleUiState, ExampleUiEvent, ExampleUiEffect, ExampleViewModel>(
 *             effectHandler = { effect -> /* handle navigation etc. */ }
 *         ) { state, onEvent ->
 *             ExampleScreen(state = state, onEvent = onEvent)
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseComposeActivity : ComponentActivity() {

    /**
     * 设置 Compose MVVM+UDF 内容。
     *
     * @param effectHandler 可选，处理一次性副作用（导航、Toast 等）。若不传，副作用由子 Composable 自行处理。
     * @param content Composable 内容，接收 uiState 和 onEvent 回调。
     */
    @Composable
    inline fun <reified S : UiState, reified E : UiEvent, reified F : UiEffect, reified VM : BaseViewModel<S, E, F>>
            MVVMContent(
        noinline effectHandler: ((F) -> Unit)? = null,
        crossinline content: @Composable (state: S, onEvent: (E) -> Unit) -> Unit
    ) {
        val viewModel: VM = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        androidx.compose.runtime.LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                effectHandler?.invoke(effect)
            }
        }

        content(uiState) { event -> viewModel.onEvent(event) }
    }
}
