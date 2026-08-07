package com.robin.baseframe.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.robin.baseframe.R

/** Shared loading, empty, error and content state surface for detail demos. */
class DemoStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val progress = CircularProgressIndicator(context).apply {
        isIndeterminate = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }
    private val title = TextView(context).apply {
        setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface))
        setTextSize(18f)
        textAlignment = TEXT_ALIGNMENT_CENTER
    }
    private val message = TextView(context).apply {
        setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant))
        setTextSize(14f)
        textAlignment = TEXT_ALIGNMENT_CENTER
    }
    private val action = Button(context).apply {
        minHeight = resources.getDimensionPixelSize(R.dimen.min_touch_target)
    }
    private val content = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(
            resources.getDimensionPixelSize(R.dimen.space_24),
            resources.getDimensionPixelSize(R.dimen.space_24),
            resources.getDimensionPixelSize(R.dimen.space_24),
            resources.getDimensionPixelSize(R.dimen.space_24)
        )
    }

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground))
        addView(
            content,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        )
        action.visibility = View.GONE
        content.addView(progress)
        content.addView(title, marginParams(R.dimen.space_12))
        content.addView(message, marginParams(R.dimen.space_8))
        content.addView(action, marginParams(R.dimen.space_12))
        showLoading()
    }

    fun showLoading(messageText: CharSequence = context.getString(R.string.demo_state_loading_message)) {
        progress.visibility = View.VISIBLE
        title.setText(R.string.demo_state_loading_title)
        message.text = messageText
        action.visibility = View.GONE
        showState()
    }

    fun showEmpty(messageText: CharSequence = context.getString(R.string.demo_state_empty_message)) {
        progress.visibility = View.GONE
        title.setText(R.string.demo_state_empty_title)
        message.text = messageText
        action.visibility = View.GONE
        showState()
    }

    fun showError(
        messageText: CharSequence,
        retryText: CharSequence = context.getString(R.string.demo_state_retry),
        onRetry: (() -> Unit)? = null
    ) {
        progress.visibility = View.GONE
        title.setText(R.string.demo_state_error_title)
        message.text = messageText
        action.text = retryText
        action.visibility = if (onRetry == null) View.GONE else View.VISIBLE
        action.setOnClickListener { onRetry?.invoke() }
        showState()
    }

    fun showContent() {
        visibility = View.GONE
    }

    private fun showState() {
        visibility = View.VISIBLE
        announceForAccessibility("${title.text}. ${message.text}")
    }

    private fun marginParams(top: Int): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = resources.getDimensionPixelSize(top)
        }
}
