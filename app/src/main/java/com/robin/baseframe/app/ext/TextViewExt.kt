package com.robin.baseframe.app.ext

import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.robin.baseframe.app.ext.dsl.DslSpannableStringBuilderImpl
import com.robin.baseframe.app.ext.dsl.TextWatcherDslImpl

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


fun SpannableStringBuilder.appendClickable(
    text: CharSequence?,
    @ColorInt color: Int? = null,
    isUnderlineText: Boolean = true,
    onClick: (View) -> Unit
): SpannableStringBuilder = inSpans(ClickableSpan(color, isUnderlineText, onClick)) { append(text) }

fun ClickableSpan(
    @ColorInt color: Int? = null,
    isUnderlineText: Boolean = true,
    onClick: (View) -> Unit,
): ClickableSpan = object : ClickableSpan() {
    override fun onClick(widget: View) = onClick(widget)

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color ?: ds.linkColor
        ds.isUnderlineText = isUnderlineText
    }
}

/*
* 文字展开，收缩
* */
fun TextView.setExpandableText(content: CharSequence, maxLine: Int, expandText: String, shrinkText: String, isExpand: Boolean = false) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            val availableWith = width - compoundPaddingLeft - compoundPaddingRight
            val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(content, 0, content.length, paint, availableWith).build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(content, 0, content.length, paint, availableWith, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false)
            }
            if (layout.lineCount > maxLine) {
                val lastLineStart = layout.getLineStart(maxLine - 1)
                val ellipsize = content.subSequence(0, lastLineStart).toString() +
                        TextUtils.ellipsize(
                            content.subSequence(lastLineStart, content.length), paint, availableWith - paint.measureText(expandText), TextUtils.TruncateAt.END
                        )
                setText(isExpand, ellipsize)
                movementMethod = LinkMovementMethod.getInstance()
            } else {
                text = content
            }
        }

        private fun setText(isExpand: Boolean, ellipsize: CharSequence) {
            text = buildSpannedString {
                append(if (isExpand) content else ellipsize)
                appendClickable(if (isExpand) shrinkText else expandText, isUnderlineText = false) {
                    setText(!isExpand, ellipsize)
                }
            }
        }
    })
}
