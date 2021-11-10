package com.robin.baseframe.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.view.clickNoRepeat
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentAnyLayerBinding
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.dialog.DialogLayer
import per.goweii.anylayer.ext.SimpleAnimatorCreator
import per.goweii.anylayer.guide.GuideLayer
import per.goweii.anylayer.guide.GuideLayer.Mapping
import per.goweii.anylayer.popup.PopupLayer
import per.goweii.anylayer.widget.SwipeLayout

class AnyLayerFragment : BaseFragment<BaseViewModel, FragmentAnyLayerBinding>() {
    private var mPopup: DialogLayer? = null
    override fun initView(savedInstanceState: Bundle?) {
        binding.btDrag.clickNoRepeat {
            AnyLayer.dialog(mActivity)
                .setContentView(R.layout.dialog_drag)
                .setBackgroundDimDefault()
                .setGravity(Gravity.LEFT)
                .setSwipeDismiss(SwipeLayout.Direction.LEFT)
                .addOnClickToDismiss(R.id.dialog_drag_h_tv_close)
                .addDataBindCallback {

                }
                .show()
        }
        binding.btToast.onClick {
            AnyLayer.toast()
                .setIcon(R.drawable.ic_success)
                .setMessage("哈哈，成功了")
                .setTextColorInt(Color.WHITE)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setGravity(Gravity.CENTER)
                .setDuration(1500L)
                .show()
        }
        binding.btNotification.clickNoRepeat {
            AnyLayer.notification(mActivity)
                .setTitle("Notification Title")
                .setIcon(R.mipmap.ic_launcher)
                .setDesc(R.string.dialog_msg)
                .setLabel("this is Label")
                .addOnNotificationClickListener { layer, view ->
                    LogUtils.debugInfo("Notification Click")
                }
                .show()

        }
        binding.btPopup.clickNoRepeat {
            if (mPopup == null) {
                mPopup = PopupLayer(binding.btPopup)
                    .setAlign(
                        PopupLayer.Align.Direction.VERTICAL,
                        PopupLayer.Align.Horizontal.CENTER,
                        PopupLayer.Align.Vertical.ABOVE,
                        false
                    )
                    .setOutsideInterceptTouchEvent(false)
                    .setContentView(R.layout.popup_normal)
                    .setAnimStyle(DialogLayer.AnimStyle.TOP)
            }
            LogUtils.debugInfo("isShow:" + mPopup!!.isShown)
            if (mPopup!!.isShown) {
                mPopup?.dismiss()
            } else {
                mPopup?.show()
            }

        }
        binding.btDialogAlpha.clickNoRepeat {
            AnyLayer.dialog(mActivity)
                .setContentView(R.layout.dialog_normal)
                .setBackgroundDimDefault()
                .setContentAnimator(SimpleAnimatorCreator(SimpleAnimatorCreator.AnimStyle.ZOOM_ALPHA))
//                .addOnClickToDismiss(R.id.fl_dialog_yes, R.id.fl_dialog_no)
                .addOnClickListener({ layer, view ->
                    when (view.id) {
                        R.id.fl_dialog_yes -> LogUtils.debugInfo("click ok")
                        R.id.fl_dialog_no -> LogUtils.debugInfo("click no")
                    }
                    layer.dismiss()
                }, R.id.fl_dialog_yes, R.id.fl_dialog_no)
                .show()

        }
        binding.btGuide.clickNoRepeat {
            showGuide()

        }
    }

    private fun showGuide() {
        val textView = TextView(mActivity)
        textView.text = "引导页面"
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16f
        val textView3 = TextView(mActivity)
        textView3.text = "下一个"
        textView3.setPadding(90, 30, 90, 30)
        textView3.setBackgroundResource(R.drawable.shape_icon)
        textView3.setTextColor(Color.WHITE)
        textView3.textSize = 16f
        GuideLayer(mActivity)
            .addMapping(
                Mapping()
                    .setTargetView(binding.btGuide)
                    .setGuideView(textView)
                    .setCornerRadius(999f)
                    .setPaddingLeft(30)
                    .setPaddingRight(30)
                    .setPaddingTop(30)
                    .setPaddingBottom(30)
                    .setHorizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    .setVerticalAlign(GuideLayer.Align.Vertical.BELOW)
            )
            .addMapping(
                Mapping()
                    .setGuideView(textView3)
                    .setHorizontalAlign(GuideLayer.Align.Horizontal.CENTER_PARENT)
                    .setVerticalAlign(GuideLayer.Align.Vertical.ALIGN_PARENT_BOTTOM)
                    .setMarginBottom(60).addOnClickListener({ layer, v ->
                        layer.dismiss()

                    })
            )
            .show()
    }
}