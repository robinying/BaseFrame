package com.robin.baseframe.ui.fragment

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.databinding.FragmentWallpaperBinding
import com.robin.baseframe.service.MyWallpaperService

class WallpaperFragment : BaseFragment<BaseViewModel, FragmentWallpaperBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(mActivity)
        try {
            val bitmap = ContextCompat.getDrawable(mActivity, R.drawable.ic_launcher_background)?.toBitmap()
            wallpaperManager.setBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLive() {
        val localIntent = Intent()
        localIntent.action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
        localIntent.putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(mActivity.packageName, MyWallpaperService::class.java.name)
        )
        startActivity(localIntent)
    }
}