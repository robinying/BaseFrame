package com.robin.baseframe.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class FlowTest {

    fun main() {
        runBlocking {
            val flow = flowOf(1, 2, 3, 4, 5, 6)
            /*
            * map就是用于将一个值映射成另一个值，
            * 具体映射的规则我们则可以在map函数中自行定义。
            * */
            flow.flowOn(Dispatchers.Main)
                .map {
                    it * it
                }
                .collect {
                    println("$it")
                }

            /*
            * filter也是一个非常简单的操作符函数。
            * 顾名思义，它是用来过滤掉一些数据的。
            * */
            flow.filter {
                it % 2 == 0
            }.map {
                it * it
            }.collect {
                println(it)
            }

            flow.onEach {
                println(it)
            }.collect {
            }

            /*
            * debounce函数可以用来确保flow的各项数据之间存在一定的时间间隔，
            * 如果是时间点过于临近的数据只会保留最后一条。
            * */
            flow {
                emit(1)
                emit(2)
                delay(600)
                emit(3)
                delay(100)
                emit(4)
                delay(200)
                emit(5)
            }.debounce(500)
                .collect {
                    println("$it")
                }

            /*
            *sample是采样的意思，也就是说，
            * 它可以从flow的数据流当中按照一定的时间间隔来采样某一条数据
            * */
            flow {
                while (true) {
                    emit("send a toast")
                }
            }.sample(1000)
                .collect {
                    println("$it")
                }

            /*
            * 自己就能终结整个flow流程的操作符函数，这种操作符函数也被称为终端操作符函数。
            * reduce就是这样的操作符
            * reduce函数会通过参数给我们一个Flow的累积值和一个Flow的当前值，
            * 我们可以在函数体中对它们进行一定的运算，
            * 运算的结果会作为下一个累积值继续传递到reduce函数当中。
            * */

            var result = flow {
                for (i in 1..100) {
                    emit(i)
                }
            }.reduce { acc, value -> acc + value }
            println("$result")

            /*
            * fold函数和reduce函数基本上是完全类似的，它也是一个终端操作符函数。
            * 主要的区别在于，fold函数需要传入一个初始值，
            * 这个初始值会作为首个累积值被传递到fold的函数体当中
            * */

            val words = flow {
                for (i in 'A'..'Z') {
                    emit(i)
                }
            }.fold("English word:") { acc, value -> acc + value }

            /*
            * flatMap的核心，就是将两个flow中的数据进行映射、合并、压平成一个flow，
            * 最后再进行输出。
            * */
            flowOf(1, 2, 3)
                .flatMapConcat {
                    flowOf("a$it", "b$it")
                }.collect {
                    println("it")
                }
            //网络请求使用flatMapConcat串连成链式执行的任务
            sendGetTokenRequest()
                .flatMapConcat { token ->
                    sendGetUserInfoRequest(token)
                }.collect {
                    println("$it")
                }
            /*flatMapMerge函数的区别也就呼之欲出了，
            它是可以并发着去处理数据的，其他和flatMapConcat使用一致*/

            //使用zip连接的两个flow，它们之间是并行的运行关系
            val flow1 = flowOf("a", "b", "c")
            val flow2 = flowOf(1, 2, 3, 4, 5)
            flow1.zip(flow2) { a, b ->
                a + b
            }.collect {
                println(it)
            }
        }
    }

    fun sendGetTokenRequest(): Flow<String> = flow {
        // send request to get token
        emit("token")
    }

    fun sendGetUserInfoRequest(token: String): Flow<String> = flow {
        // send request with token to get user info
        emit("userInfo")
    }
}
