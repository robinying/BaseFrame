package com.robin.baseframe.widget.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper

open class BaseDialog(context: Context): AlertDialog(context),DialogInterceptor{
    private var mChain: DialogChain? = null

    /*下一个拦截器*/
    fun chain(): DialogChain? = mChain

    @CallSuper
    override fun intercept(chain: DialogChain) {
        mChain = chain
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.attributes?.width=800
        window?.attributes?.height=900

    }
}