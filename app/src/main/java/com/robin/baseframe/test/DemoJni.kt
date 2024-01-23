package com.robin.baseframe.test

import android.util.Log

class DemoJni {

    companion object {
        const val TAG = "HelloJNI"

        // 如果使用 const val 或 static final 修饰（静态常量），则无法从 Native 层修改
        private val sName = "初始值"
        fun getsName(): String {
            return sName
        }


        init {
            // 加载 so 库
            System.loadLibrary("demo-jni")
        }
    }

    external fun sayHi()

    external fun accessField()

     fun callFromJava() {
         Log.d("robinTest","call from java")
     }

    external fun test()
}