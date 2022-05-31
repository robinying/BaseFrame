package com.robin.aidldemo;

import com.robin.aidldemo.Apple;

interface IRemoteServiceCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void noticeAppleInfo(inout Apple apple);
}
