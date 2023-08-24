package com.robin.baseframe.camera

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView : GLSurfaceView {

    private val cameraXController: CameraXController = CameraXController()

    private val callback = object : MyGLRenderCallback {
        override fun onSurfaceChanged() {
            //关联并绑定CameraX
            setUpCamera()
        }

        override fun onFrameAvailable() {
            //确认渲染
            requestRender()
        }
    }

    // GLSurfaceView.Renderer 渲染对象
    private val cameraRender = MyGLRender(context, callback)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setEGLContextClientVersion(2)

        //设置GL渲染对象
        setRenderer(cameraRender)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private fun setUpCamera() {
        cameraXController.setUpCamera(context, cameraRender)
    }

    fun getCameraXController(): CameraXController {
        return cameraXController
    }
}