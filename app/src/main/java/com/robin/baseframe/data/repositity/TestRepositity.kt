package com.robin.baseframe.data.repositity

import kotlinx.coroutines.delay

class TestRepositity : CacheRepositity<String>() {
    override suspend fun fetchDataFromLocal(): CResult<String> {
        delay(1000)
        return CResult.Success("data from local")
    }

    override suspend fun fetchDataFromNetWork(): CResult<String> {
        delay(2000)
        return CResult.Success("data from network")
    }
}