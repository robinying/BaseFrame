package com.robin.baseframe.camera

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.camera.view.PreviewView
import java.io.File

class CameraXActionImpl :ICameraAction {

    private var mVideoRecordFile: File? = null // 输出的文件
    private lateinit var mContext: Context
    private val mCameraCallback: ICameraCallback? = null
    private val cameraXController: CameraXController = CameraXController()
    private lateinit var mPreviewView: PreviewView

    override fun setOutFile(file: File) {
        mVideoRecordFile = file
    }

    override fun getOutFile(): File {
        return mVideoRecordFile!!
    }

    override fun initCamera(context: Context): View {
        mPreviewView = PreviewView(context)
        mContext = context
        mPreviewView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        mPreviewView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (mPreviewView.isShown) {
                    startCamera()
                }
                mPreviewView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        return mPreviewView
    }

    private fun startCamera() {
        cameraXController.setUpCamera(mContext, mPreviewView.surfaceProvider)
    }

    override fun initCameraRecord() {}
    override fun startCameraRecord() {
        cameraXController.startCameraRecord(getOutFile())
    }

    override fun stopCameraRecord(cameraCallback: ICameraCallback) {
        cameraXController.stopCameraRecord(cameraCallback)
    }

    override fun releaseCameraRecord() {}
    override fun releaseAllCamera() {}
    override fun clearWindow() {}
    override fun isShowCameraView(isVisible: Boolean) {
        mPreviewView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}