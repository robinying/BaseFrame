package com.robin.baseframe.app.event

import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.base.arch.EmptyUiEffect
import com.robin.baseframe.base.arch.EmptyUiEvent
import com.robin.baseframe.base.arch.EmptyUiState

class AppViewModel : BaseViewModel<EmptyUiState, EmptyUiEvent, EmptyUiEffect>(EmptyUiState()) {

    override fun onEvent(event: EmptyUiEvent) {
        // App 级 ViewModel，无页面事件
    }

    init {

    }
}