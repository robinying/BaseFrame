package com.robin.baseframe.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import com.robin.baseframe.bean.Offset

class MoveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var lastOffset = Offset(0f, 0f)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN->{
                lastOffset = Offset(event.x,event.y)
            }
            MotionEvent.ACTION_MOVE->{
                val dx = event.x -lastOffset.x
                val dy = event.y - lastOffset.y
                ViewCompat.offsetLeftAndRight(this,dx.toInt())
                ViewCompat.offsetTopAndBottom(this,dy.toInt())
            }

        }
        return true
    }
}