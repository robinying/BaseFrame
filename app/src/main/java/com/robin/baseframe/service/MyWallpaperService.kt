package com.robin.baseframe.service

import android.hardware.Camera
import android.media.MediaPlayer
import android.os.Bundle
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.robin.baseframe.R
import java.io.IOException

/*
* 壁纸作为一个系统服务，在系统启动时，不管是动态壁纸还是静态壁纸，都会以一个Service的
* 形式运行在后台——WallpaperService，它的Window类型为TYPE_WALLPAPER，
* WallpaperService提供了一个SurfaceHolder来暴露给外界来对画面进行渲染，这就是设置壁纸的基本原理。

* 创建一个动态壁纸，需要继承系统的WallpaperService，并提供一个WallpaperService.Engin来进行渲染，
* 下面这个就是一个模板代码。
* */
class MyWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = WallpaperEngine()

    inner class WallpaperEngine : WallpaperService.Engine() {
        lateinit var mediaPlayer: MediaPlayer

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.testwallpaper).also {
                it.setSurface(holder!!.surface)
                it.isLooping = true
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    inner class CameraWallpaperEngine : WallpaperService.Engine() {
        lateinit var camera: Camera

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                startPreview()
            } else {
                stopPreview()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            stopPreview()
        }

        private fun startPreview() {
            camera = Camera.open()
            camera.setDisplayOrientation(90)
            try {
                camera.setPreviewDisplay(surfaceHolder)
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun stopPreview() {
            try {
                camera.stopPreview()
                camera.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}