package com.robin.baseframe.viewmodel

import androidx.lifecycle.LiveData
import com.robin.baseframe.utils.CountDataChangeListener
import com.robin.baseframe.utils.CountDownManager

class GlobalLiveData:LiveData<String>() {
    companion object {
        private lateinit var instance: GlobalLiveData
        fun getInstance() = if (::instance.isInitialized) instance else GlobalLiveData()
    }

    private val mListener = object : CountDataChangeListener {
        override fun onChange(msg: String) {
            postValue(msg)  // 调用更新数据的方法
        }
    }

    fun startCount(remainSecond: Long? = 10L) { CountDownManager.startCount(remainSecond) }

    fun cancelCount() { CountDownManager.cancelCount() }

    // 活跃时添加监听器
    override fun onActive() {
        super.onActive()
        CountDownManager.setListener(mListener)
    }

    // 不活跃时移除监听器
    override fun onInactive() {
        super.onInactive()
        CountDownManager.removeListener(mListener)
    }
}