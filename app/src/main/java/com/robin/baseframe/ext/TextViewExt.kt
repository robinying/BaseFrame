package com.robin.baseframe.ext

import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.robin.baseframe.ext.dsl.DslSpannableStringBuilderImpl
import com.robin.baseframe.ext.dsl.TextWatcherDslImpl

fun TextView.addTextChangedListenerDsl(init: TextWatcherDslImpl.() -> Unit) {
    val listener = TextWatcherDslImpl()
    listener.init()
    this.addTextChangedListener(listener)
}

inline fun TextView.addTextChangedListenerClosure(
    crossinline afterTextChanged: (Editable?) -> Unit = {},
    crossinline beforeTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { charSequence, start, count, after -> },
    crossinline onTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { charSequence, start, after, count -> }
) {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s, start, before, count)
        }
    }
    this.addTextChangedListener(listener)
}

interface DslSpannableStringBuilder {
    //增加一段文字
    fun addText(text: String, method: (DslSpanBuilder.() -> Unit)? = null)
}

interface DslSpanBuilder {
    //设置文字颜色
    fun setColor(color: String)
    //设置点击事件
    fun onClick(useUnderLine: Boolean = true, onClick: (View) -> Unit)
}

//为 TextView 创建扩展函数，其参数为接口的扩展函数
fun TextView.buildSpannableString(init: DslSpannableStringBuilder.() -> Unit) {
    //具体实现类
    val spanStringBuilderImpl = DslSpannableStringBuilderImpl()
    spanStringBuilderImpl.init()
    movementMethod = LinkMovementMethod.getInstance()
    //通过实现类返回SpannableStringBuilder
    text = spanStringBuilderImpl.build()
}
