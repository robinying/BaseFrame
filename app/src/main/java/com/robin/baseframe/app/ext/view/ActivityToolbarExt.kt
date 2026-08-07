package com.robin.baseframe.app.ext.view

import android.content.Context
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.robin.baseframe.R

fun View.bindActivityToolbar() {
    val toolbar = findViewById<Toolbar>(R.id.tool_bar) ?: return
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    toolbar.setNavigationContentDescription(R.string.navigate_up)
    toolbar.setNavigationOnClickListener { context.findActivity()?.finish() }
}

private fun Context.findActivity(): android.app.Activity? {
    var current: Context = this
    while (current is android.content.ContextWrapper) {
        if (current is android.app.Activity) return current
        current = current.baseContext
    }
    return null
}
