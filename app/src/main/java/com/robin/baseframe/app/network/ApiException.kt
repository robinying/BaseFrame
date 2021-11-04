package com.robin.baseframe.app.network

class ApiException(val msg: String, val status: Int) :
    Throwable()