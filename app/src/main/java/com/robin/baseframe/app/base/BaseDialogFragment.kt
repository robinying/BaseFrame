package com.robin.baseframe.app.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.robin.baseframe.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 基础 DialogFragment — 不依赖 ViewModel。
 * 需要 MVVM+UDF 的弹窗，子类自行使用 @AndroidEntryPoint + Hilt。
 */
abstract class BaseDialogFragment(@LayoutRes layoutID: Int) : DialogFragment(layoutID),
    CoroutineScope by MainScope() {

    init {
        setStyle(STYLE_NORMAL, R.style.CommonDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFragmentCreated(view, savedInstanceState)
        observeLiveBus()
    }

    abstract fun onFragmentCreated(view: View, savedInstanceState: Bundle?)

    override fun show(manager: FragmentManager, tag: String?) {
        kotlin.runCatching {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    protected fun execute(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(context = Dispatchers.Main) {
        kotlinx.coroutines.withContext(context) { block() }
    }

    open fun observeLiveBus() {}
}
