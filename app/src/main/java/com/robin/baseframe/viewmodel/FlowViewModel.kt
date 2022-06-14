package com.robin.baseframe.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.data.bean.Banner
import com.robin.baseframe.data.bean.Channel
import com.robin.baseframe.data.bean.ListBean
import com.robin.baseframe.data.repositity.Test2Repo
import com.robin.baseframe.data.repositity.TestRepositity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowViewModel : BaseViewModel() {
    /*
    * 在 Java 中用通配符 ? extends T 表示协变，extends 限制了父类型 T，其中 ? 表示未知类型，
    * 比如 ? extends Number，只要传入的类型是 Number 或者 Number 的子类型都可以
    *
    *  在 Kotlin 中关键字 out T 表示协变，含义和 Java 一样
    * */
    //java List<? extends Number> list = new ArrayList<Integer>()
    val numbers:MutableList<out Number> = ArrayList<Int>()

    val loadStatus: MutableLiveData<String> = MutableLiveData()
    val dataInt: MutableLiveData<Int> = MutableLiveData()

    private val bannerFlow = MutableStateFlow<Banner?>(null)
    private val channelFlow = MutableStateFlow<Channel?>(null)
    private val listFlow = MutableStateFlow<ListBean?>(null)

    fun coldFlowDemo() {
        val coldFlow = flow<Int> {
            for (i in 0..5) {
                delay(1000)
                emit(i)
            }
        }
        launchOnIO {
            coldFlow.onStart {
                //不是主线程的需要用postValue
                loadingChange.showDialog.postValue("start loading")
                loadStatus.postValue("start loading")
            }.onEmpty {
                loadingChange.dismissDialog.postValue(true)
                loadStatus.postValue("empty")
            }.catch {
                loadingChange.dismissDialog.postValue(true)
                loadStatus.postValue("load error${it.message}")
            }.onCompletion {
                loadingChange.dismissDialog.postValue(true)
                loadStatus.postValue("start complete")
            }.flowOn(Dispatchers.IO)
                .collect {
                    dataInt.postValue(it)
                }
        }

    }

    fun cacheRepositityDemo() {
        val testRepositity = TestRepositity()
        launchOnIO {
            testRepositity.getData()
                .onStart {
                    LogUtils.debugInfo("start get cache data")
                }.onCompletion {
                    LogUtils.debugInfo("get cache data complete")
                }.flowOn(Dispatchers.IO)
                .collect {
                    LogUtils.debugInfo("get data:$it")
                }
        }
    }

    fun test2RepoDemo() {
        val test2Repo = Test2Repo()
        launchOnIO {
            test2Repo.getData()
                .onStart {
                    LogUtils.debugInfo("test2 start get cache data")
                }.onCompletion {
                    LogUtils.debugInfo("test2 get cache data complete")
                }.flowOn(Dispatchers.IO)
                .collect {
                    LogUtils.debugInfo("test2 get data:$it")
                }
        }
    }

    fun getHomeData() {
        launchOnIO {
           val banner = async {
                delay(1000)
                LogUtils.debugInfo("banner")
                bannerFlow.emit(Banner("https://www.baidu.com"))
            }
            val channel =async {
                delay(2000)
                LogUtils.debugInfo("channel")
                channelFlow.emit(Channel("yy"))
            }
            val list =async {
                delay(3000)
                LogUtils.debugInfo("list data")
                listFlow.emit(ListBean("list"))
            }
            banner.await()
            channel.await()
            list.await()
            LogUtils.debugInfo("getHomeData complete")
        }
    }

}