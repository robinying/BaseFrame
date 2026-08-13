package com.robin.baseframe.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.robin.aidldemo.Apple
import com.robin.aidldemo.IApiCallBack
import com.robin.aidldemo.IRemoteService
import com.robin.aidldemo.IRemoteServiceCallBack
import com.robin.baseframe.R
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Verifies the AIDL services can be bound and their callbacks can be released. */
@RunWith(AndroidJUnit4::class)
class AidlServiceLifecycleTest {
    private lateinit var mContext: Context
    private var mApiServiceBound = false
    private var mObserverServiceBound = false

    @Before
    fun setUp() {
        mContext = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        if (mObserverServiceBound) {
            mContext.unbindService(mObserverConnection)
            mObserverServiceBound = false
        }
        if (mApiServiceBound) {
            mContext.unbindService(mApiConnection)
            mApiServiceBound = false
        }
    }

    @Test
    fun apiServiceReturnsAppleInfoAfterBinding() {
        bindApiService()

        assertTrue(mApiConnected.await(SERVICE_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        assertEquals("蛇果", mApiService?.appleInfo?.name)
    }

    @Test
    fun observerServiceCanUnregisterCallbackAfterBinding() {
        bindObserverService()

        assertTrue(mObserverConnected.await(SERVICE_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        mObserverService?.registerCallback(mObserverCallback)
        mObserverService?.unregisterCallback(mObserverCallback)

        assertNotNull(mObserverService)
    }

    private fun bindApiService() {
        mApiServiceBound = mContext.bindService(
            Intent(mContext, RemoteService::class.java),
            mApiConnection,
            Context.BIND_AUTO_CREATE
        )
        assertTrue(mApiServiceBound)
    }

    private fun bindObserverService() {
        mObserverServiceBound = mContext.bindService(
            Intent(mContext, RemoteObserverService::class.java),
            mObserverConnection,
            Context.BIND_AUTO_CREATE
        )
        assertTrue(mObserverServiceBound)
    }

    private val mApiConnected = CountDownLatch(1)
    private val mObserverConnected = CountDownLatch(1)
    private var mApiService: IApiCallBack? = null
    private var mObserverService: IRemoteService? = null

    private val mApiConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mApiService = IApiCallBack.Stub.asInterface(service)
            mApiConnected.countDown()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mApiService = null
        }
    }

    private val mObserverConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mObserverService = IRemoteService.Stub.asInterface(service)
            mObserverConnected.countDown()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mObserverService = null
        }
    }

    private val mObserverCallback = object : IRemoteServiceCallBack.Stub() {
        override fun noticeAppleInfo(apple: Apple?) = Unit
    }

    private companion object {
        const val SERVICE_CONNECTION_TIMEOUT_SECONDS = 5L
    }
}
