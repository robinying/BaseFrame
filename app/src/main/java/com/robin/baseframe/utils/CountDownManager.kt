package com.robin.baseframe.utils

import android.os.CountDownTimer

object CountDownManager {
    private var mRemainSecond: Long = 10L
    private var mTimer: CountDownTimer? = null
    private var mListener = arrayListOf<CountDataChangeListener>()

    // 开始倒计时
    fun startCount(remainSecond: Long? = 10L) {
        mRemainSecond = remainSecond!!
        mTimer = object: CountDownTimer(remainSecond * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mRemainSecond--
                dispatchMessage("剩余：$mRemainSecond 秒")
            }

            override fun onFinish() {
                dispatchMessage("倒计时结束")
            }
        }
        mTimer!!.start()
    }

    // 取消倒计时
    fun cancelCount() {
        if(mTimer != null) {
            mTimer!!.cancel()
            mListener.clear()
        }
    }

    // 遍历回调方法
    private fun dispatchMessage(msg: String) { mListener.forEach { it.onChange(msg) } }

    // 添加监听器
    fun setListener(listener: CountDataChangeListener) { mListener.add(listener) }

    // 移除监听器
    fun removeListener(listener: CountDataChangeListener) { mListener.remove(listener) }
}

// 回调接口
interface CountDataChangeListener{
    fun onChange(msg: String)
}