package com.robin.baseframe.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseViewFragment
import com.robin.baseframe.app.ext.util.notificationManager
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.databinding.FragmentNotificationBinding
import com.robin.baseframe.ui.activity.MainActivity

class NotificationFragment : BaseViewFragment<FragmentNotificationBinding>() {
    private val mNormalChannelId = "normal_channel"
    private val mNormalChannelName = "normal_name"
    private val mNormalNotificationId = 100
    private val mHighChannelId = "high_channel"
    private val mHighChannelName = "high_name"
    private val mHighNotificationId = 101
    private val mProgressChannelId = "progress_channel"
    private val mProgressChannelName = "progress_name"
    private val mProgressNotificationId = 102
    private var mBuilder: NotificationCompat.Builder? = null
    private val mManager by lazy { mActivity.notificationManager }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            updatePermissionState(granted)
        }

    override fun initView(savedInstanceState: Bundle?) {
        binding.btNormalNotification.onClick {
            createNotificationForNormal()
        }
        binding.btHighNotification.onClick {
            Handler().postDelayed({
                createNotificationForHigh()
            }, 10000)

        }
        binding.btProgressNotification.onClick {
            createNotificationForProgress()
            binding.btProgressNotification.postDelayed({
                updateNotificationForProgress()
            }, 3000)
        }

        if (hasNotificationPermission()) {
            updatePermissionState(true)
        } else {
            updatePermissionState(false)
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            mActivity, android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** 根据通知权限状态启用/禁用发送入口，并展示被拒绝时的说明文案。 */
    private fun updatePermissionState(granted: Boolean) {
        binding.btNormalNotification.isEnabled = granted
        binding.btHighNotification.isEnabled = granted
        binding.btProgressNotification.isEnabled = granted
        binding.tvPermissionHint.visibility = if (granted) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun createNotificationForNormal() {
        // 适配8.0及以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(mNormalChannelId, mNormalChannelName, NotificationManager.IMPORTANCE_LOW).apply {
                    description = getString(R.string.notification_channel_description)
                    setShowBadge(false) // 是否在桌面显示角标
                }
            mManager?.createNotificationChannel(channel)
        }
        // 点击意图 // setDeleteIntent 移除意图
        val intent = Intent(mActivity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        // 构建配置
        mBuilder = NotificationCompat.Builder(mActivity, mNormalChannelId)
            .setContentTitle(getString(R.string.notification_title_normal)) // 标题
            .setContentText(getString(R.string.notification_content_normal)) // 文本
            .setSmallIcon(R.mipmap.ic_launcher) // 小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye)) // 大图标
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 7.0 设置优先级
            .setContentIntent(pendingIntent) // 跳转配置
            .setAutoCancel(true) // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
        // 发起通知
        mManager?.notify(mNormalNotificationId, mBuilder?.build())
    }

    private fun createNotificationForHigh() {
        val intent = Intent(mActivity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(mHighChannelId, mHighChannelName, NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            channel.enableLights(true)
            mManager?.createNotificationChannel(channel)
        }
        mBuilder = NotificationCompat.Builder(mActivity, mHighChannelId)
            .setContentTitle(getString(R.string.notification_title_high))
            .setContentText(getString(R.string.notification_content_high))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye))
            //.setOngoing(true)
            .setAutoCancel(false)
            .setNumber(9) // 自定义桌面通知数量
            .addAction(R.drawable.ic_eye, getString(R.string.notification_action_view), pendingIntent)// 通知上的操作
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE) // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏
        mManager?.notify(mHighNotificationId, mBuilder?.build())
    }

    private fun createNotificationForProgress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    mProgressChannelId,
                    mProgressChannelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.notification_channel_description)
                    setShowBadge(true) // 是否在桌面显示角标
                }
            mManager?.createNotificationChannel(channel)
        }
        val progressMax = 100
        val progressCurrent = 30
        mBuilder = NotificationCompat.Builder(requireContext(), mProgressChannelId)
            .setContentTitle(getString(R.string.notification_title_progress))
            .setContentText(getString(R.string.notification_content_progress_format, progressCurrent))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye))
            // 第3个参数indeterminate，false表示确定的进度，比如100，true表示不确定的进度，会一直显示进度动画，直到更新状态下载完成，或删除通知
            .setProgress(progressMax, progressCurrent, false)

        mManager?.notify(mProgressNotificationId, mBuilder?.build())
    }

    private fun updateNotificationForProgress() {
        if (mBuilder != null) {
            val progressMax = 100
            val progressCurrent = 50
            mBuilder?.let { builder ->
                // 1.更新进度
                builder.setContentText(getString(R.string.notification_content_progress_format, progressCurrent))
                    .setProgress(progressMax, progressCurrent, false)
                // 2.下载完成
                //builder.setContentText("下载完成！").setProgress(0, 0, false)
                mManager?.notify(mProgressNotificationId, builder.build())
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.notification_toast_progress_updated, progressCurrent),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.notification_toast_progress_missing), Toast.LENGTH_SHORT).show()
        }
    }
}