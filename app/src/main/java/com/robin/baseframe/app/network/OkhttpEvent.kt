package com.robin.baseframe.app.network

class OkhttpEvent {
    /*
    *DNS解析开始时间
    * */
    var dnsStartTime = 0L

    /*
    * DNS解析结束时间
    * */
    var dnsEndTime = 0L

    /*
    * 响应体大小
    * */
    var responseBodySize = 0L

    /*
    *是否请求成功
    * */
    var isSuccess = false

    /*
    * 错误信息
    * */
    var errorStack:String?=null
}