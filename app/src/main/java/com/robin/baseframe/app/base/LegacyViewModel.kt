package com.robin.baseframe.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robin.baseframe.app.callback.livedata.event.EventLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 旧版 ViewModel 兼容层。
 *
 * 保留旧 BaseViewModel 的 API（loadingChange / launchOnIO / launchOnMain），
 * 供存量页面在迁移到 UDF 前继续使用。新代码请使用 [BaseViewModel]（带 S/E/F 泛型）。
 */
@Deprecated("Use BaseViewModel with UiState/UiEvent/UiEffect generics")
open class LegacyViewModel : ViewModel() {

    protected fun launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) { block() }
    }

    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) { block() }
    }

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框
     */
    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { EventLiveData<String>() }

        //隐藏
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }
}
