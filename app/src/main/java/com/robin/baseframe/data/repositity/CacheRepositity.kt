package com.robin.baseframe.data.repositity

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.supervisorScope

abstract class CacheRepositity<T> {
    private val TAG = "CacheRepositity"

    fun getData() = channelFlow {
        supervisorScope {
            val dataFromLocalDeffer = async {
                fetchDataFromLocal().also {
                    Log.d(TAG,"fetchDataFromLocal result:$it , thread:${Thread.currentThread().name}")
                    //本地数据加载成功
                    if (it is CResult.Success) {
                        send(it)
                    }
                }
            }

            val dataFromNetDeffer = async {
                fetchDataFromNetWork().also {
                    Log.d(TAG,"fetchDataFromNetWork result:$it , thread:${Thread.currentThread().name}")
                    //网络数据加载成功
                    if (it is CResult.Success) {
                        send(it)
                        //如果网络数据已加载，可以直接取消任务,就不需要处理本地数据了
                        dataFromLocalDeffer.cancel()
                    }
                }
            }

            //本地数据和网络数据，都加载失败的情况
            val localData = dataFromLocalDeffer.await()
            val networkData = dataFromNetDeffer.await()
            if (localData is CResult.Error && networkData is CResult.Error) {
                send(CResult.Error(Throwable("load data error")))
            }
        }
    }

    protected abstract suspend fun fetchDataFromLocal(): CResult<T>

    protected abstract suspend fun fetchDataFromNetWork(): CResult<T>

}

sealed class CResult<out R> {
    data class Success<out T>(val data: T) : CResult<T>()
    data class Error(val throwable: Throwable) : CResult<Nothing>()
}