package com.robin.baseframe.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.util.notificationManager
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.databinding.FragmentNotificationBinding
import com.robin.baseframe.ui.activity.MainActivity

class NotificationFragment : BaseFragment<BaseViewModel, FragmentNotificationBinding>() {
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
    override fun initView(savedInstanceState: Bundle?) {
        binding.btNormalNotification.onClick {
            createNotificationForNormal()
        }
        binding.btHighNotification.onClick{
            createNotificationForHigh()
        }
        binding.btProgressNotification.onClick{
            createNotificationForProgress()
            binding.btProgressNotification.postDelayed({
                updateNotificationForProgress()
            },3000)
        }
    }

    private fun createNotificationForNormal() {
        // 适配8.0及以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(mNormalChannelId, mNormalChannelName, NotificationManager.IMPORTANCE_LOW).apply {
                    description = "描述"
                    setShowBadge(false) // 是否在桌面显示角标
                }
            mManager?.createNotificationChannel(channel)
        }
        // 点击意图 // setDeleteIntent 移除意图
        val intent = Intent(mActivity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        // 构建配置
        mBuilder = NotificationCompat.Builder(mActivity, mNormalChannelId)
            .setContentTitle("普通通知") // 标题
            .setContentText("普通通知内容") // 文本
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
            mManager?.createNotificationChannel(channel)
        }
        mBuilder = NotificationCompat.Builder(mActivity, mHighChannelId)
            .setContentTitle("重要通知")
            .setContentText("重要通知内容")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye))
            //.setOngoing(true)
            .setAutoCancel(false)
            .setNumber(9) // 自定义桌面通知数量
            .addAction(R.drawable.ic_eye, "去看看", pendingIntent)// 通知上的操作
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE) // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏
        mManager?.notify(mHighNotificationId, mBuilder?.build())
    }

    private fun createNotificationForProgress(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(mProgressChannelId, mProgressChannelName, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "描述"
                    setShowBadge(true) // 是否在桌面显示角标
                }
            mManager?.createNotificationChannel(channel)
        }
        val progressMax = 100
        val progressCurrent = 30
        mBuilder = NotificationCompat.Builder(requireContext(), mProgressChannelId)
            .setContentTitle("进度通知")
            .setContentText("下载中：$progressCurrent%")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye))
            // 第3个参数indeterminate，false表示确定的进度，比如100，true表示不确定的进度，会一直显示进度动画，直到更新状态下载完成，或删除通知
            .setProgress(progressMax, progressCurrent, false)

        mManager?.notify(mProgressNotificationId, mBuilder?.build())
    }

    private fun updateNotificationForProgress() {
        if (mBuilder!=null) {
            val progressMax = 100
            val progressCurrent = 50
            // 1.更新进度
            mBuilder!!.setContentText("下载中：$progressCurrent%")
                .setProgress(progressMax, progressCurrent, false)
            // 2.下载完成
            //mBuilder.setContentText("下载完成！").setProgress(0, 0, false)
            mManager?.notify(mProgressNotificationId, mBuilder!!.build())
            Toast.makeText(requireContext(), "已更新进度到$progressCurrent%", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "请先发一条进度条通知", Toast.LENGTH_SHORT).show()
        }
    }
}