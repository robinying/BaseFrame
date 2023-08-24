package com.robin.baseframe.camera

import android.content.Context
import android.view.View
import java.io.File

interface ICameraAction {

    fun setOutFile(file: File)

    fun getOutFile(): File?

    fun initCamera(context: Context): View

    fun initCameraRecord()

    fun startCameraRecord()

    fun stopCameraRecord(cameraCallback: ICameraCallback)

    fun releaseCameraRecord()

    fun releaseAllCamera()

    fun clearWindow()

    fun isShowCameraView(isVisible: Boolean)
}