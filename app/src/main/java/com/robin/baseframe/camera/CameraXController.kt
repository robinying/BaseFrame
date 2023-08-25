package com.robin.baseframe.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.robin.baseframe.app.util.LogUtils
import java.io.File
import java.util.concurrent.Executors

@SuppressLint("RestrictedApi")
class CameraXController {
    private var preview: Preview? = null
    private var mCameraProvider: ProcessCameraProvider? = null
    private var mLensFacing = 0
    private var mCameraSelector: CameraSelector? = null
    private var mVideoCapture: VideoCapture? = null
    private var mCameraCallback: ICameraCallback? = null
    private val mExecutorService = Executors.newSingleThreadExecutor()

    //初始化 CameraX 相关配置
    fun setUpCamera(context: Context, surfaceProvider: Preview.SurfaceProvider) {

        //获取屏幕的分辨率与宽高比
        val displayMetrics = context.resources.displayMetrics
        val screenAspectRatio = aspectRatio(displayMetrics.widthPixels, displayMetrics.heightPixels)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            mCameraProvider = cameraProviderFuture.get()

            //镜头选择
            mLensFacing = lensFacing
            mCameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()

            //预览对象
            preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()
            preview?.setSurfaceProvider(surfaceProvider)


            //录制视频对象
            mVideoCapture = VideoCapture.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                //视频帧率
                .setVideoFrameRate(30)
                //bit率
                .setBitRate(3 * 1024 * 1024)
                .build()

            //绑定到页面
            rebindCamera(context)

        }, ContextCompat.getMainExecutor(context))
    }

    fun switchCamera(context: Context) : Int{
        mLensFacing = if (mLensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        mCameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()

        rebindCamera(context)

        return mLensFacing
    }

    // 切换摄像头后重新绑定到生命周期
    private fun rebindCamera(context: Context) {
        mCameraProvider?.unbindAll()
        val camera = mCameraProvider?.bindToLifecycle(
            context as LifecycleOwner,
            mCameraSelector!!,
            mVideoCapture,
            preview
        )

        val cameraInfo = camera?.cameraInfo
        val cameraControl = camera?.cameraControl
    }

    //根据屏幕宽高比设置预览比例为4:3还是16:9
    private fun aspectRatio(widthPixels: Int, heightPixels: Int): Int {
        val previewRatio = Math.max(widthPixels, heightPixels).toDouble() / Math.min(widthPixels, heightPixels).toDouble()
        return if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else {
            AspectRatio.RATIO_16_9
        }
    }

    //优先选择哪一个摄像头镜头
    private val lensFacing: Int
        private get() {
            if (hasBackCamera()) {
                return CameraSelector.LENS_FACING_BACK
            }
            return if (hasFrontCamera()) {
                CameraSelector.LENS_FACING_FRONT
            } else -1
        }

    //是否有后摄像头
    private fun hasBackCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    //是否有前摄像头
    private fun hasFrontCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }


    // 开始录制
    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun startCameraRecord(outFile: File) {
        mVideoCapture ?: return

        val outputFileOptions: VideoCapture.OutputFileOptions = VideoCapture.OutputFileOptions.Builder(outFile).build()

        mVideoCapture!!.startRecording(outputFileOptions, mExecutorService, object : VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                LogUtils.debugInfo("视频保存成功,outputFileResults:" + outputFileResults.savedUri)
                mCameraCallback?.takeSuccess()
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                LogUtils.debugInfo(message)
            }
        })
    }

    // 停止录制
    fun stopCameraRecord(cameraCallback: ICameraCallback?) {
        mCameraCallback = cameraCallback
        mVideoCapture?.stopRecording()
    }

    // 释放资源
    fun releaseAll() {
        mVideoCapture?.stopRecording()
        mExecutorService.shutdown()
        mCameraProvider?.unbindAll()
        mCameraProvider?.shutdown()
        mCameraProvider = null
    }
}