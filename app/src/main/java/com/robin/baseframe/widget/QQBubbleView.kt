package com.robin.baseframe.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.robin.baseframe.R

class QQBubbleView : View {
    /**
     * 默认状态
     */
    private val BUBBLE_STATE_DEFAULT = 0X01

    /**
     * 手指点击到圆上的状态
     */
    private val BUBBLE_STATE_CLICK = 0X02

    /**
     * 移动的状态 运动球跟静止球分离
     */
    private val BUBBLE_STATE_BREAK = 0X03

    /**
     * 爆炸消失的状态
     */
    private val BUBBLE_STATE_BLAST = 0X04

    /**
     * 气泡移动完后回到原来状态
     */
    val EXECUTE_STATE_BACK = 0X05

    /**
     * 气泡移动完后回爆炸消失
     */
    val EXECUTE_STATE_BLAST = 0X06

    /**
     * 小球的画笔 文本的画笔 爆炸效果的画笔
     */
    private var mBubblePaint: Paint? = null
    /**
     * 小球的画笔 文本的画笔 爆炸效果的画笔
     */
    private  var mTextPaint:Paint? = null
    /**
     * 小球的画笔 文本的画笔 爆炸效果的画笔
     */
    private  var mBlastPaint:Paint? = null

    /**
     * 移动圆的半径
     */
    private var mMoveRadius = 40f

    /**
     * 静止圆的半径
     */
    private var mQuitRadius = 40f

    /**
     * 静止圆的圆心点
     */
    private var mQuitPoint: PointF? = null

    /**
     * 移动的圆的圆心点
     */
    private var mMovePoint: PointF? = null
    private var mText = "22"
    private var mTextRect: Rect? = null

    /**
     * 当前的状态
     */
    private var mState = BUBBLE_STATE_DEFAULT

    /**
     * 手指点击到圆附近的偏移
     */
    private val MOVE_OFFSET = mMoveRadius / 4

    /**
     * 最大移动距离 静止球消失
     */
    private var mMaxDist = 8 * mMoveRadius

    /**
     * 手指点击的坐标和静止圆的原点之间的距离
     */
    private var mDist = 0f

    private var mBezierPath: Path? = null

    /**
     * 气泡爆炸图片
     */
    private val mBlastDrawablesArray = intArrayOf(
        R.drawable.burst_1,
        R.drawable.burst_2,
        R.drawable.burst_3,
        R.drawable.burst_4,
        R.drawable.burst_5
    )

    /**
     * 气泡爆炸的bitmap数组
     */
    private lateinit var mBlastBitmapsArray: Array<Bitmap?>

    /**
     * 爆炸图片的位置
     */
    private var mBlastIndex = 0

    /**
     * 爆炸的区域
     */
    private var mBlastRect: RectF? = null

    private var mOnExecuteFinishListener: OnExecuteFinishListener? = null


    fun setOnDismissListener(onDismissListener: OnExecuteFinishListener?) {
        mOnExecuteFinishListener = onDismissListener
    }

    constructor (context: Context?) : super(context) {

    }

    constructor (context: Context?,  attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        init()
    }

    private fun init() {
        mBubblePaint = Paint()
        mBubblePaint!!.isAntiAlias = true
        mBubblePaint!!.style = Paint.Style.FILL
        mBubblePaint!!.color = Color.RED
        mMovePoint = PointF()
        mQuitPoint = PointF()
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.style = Paint.Style.STROKE
        mTextPaint!!.textSize = 18f
        mTextPaint!!.color = Color.WHITE
        mTextRect = Rect()
        mBlastPaint = Paint()
        mBlastPaint!!.isAntiAlias = true
        mBlastPaint!!.isFilterBitmap = true
        mBezierPath = Path()
        mBlastBitmapsArray = arrayOfNulls(5)
        for (i in mBlastDrawablesArray.indices) {
            mBlastBitmapsArray[i] = BitmapFactory.decodeResource(resources, mBlastDrawablesArray[i])
        }
        mBlastRect = RectF()
    }

    fun setText(text: String) {
        mText = text
    }

    /**
     * 设置圆心的坐标
     */
    fun setCenter(x: Float, y: Float, radius: Int) {
        mState = BUBBLE_STATE_CLICK
        mQuitPoint!![x] = y
        mMovePoint!![x] = y
        mMoveRadius = radius.toFloat()
        mQuitRadius = radius.toFloat()
        mMaxDist = mMoveRadius * 8
        invalidate()
    }

    fun setState(state: Int) {
        mState = state
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        mQuitPoint.set(w / 2, h / 2);
//        mMovePoint.set(w / 2, h / 2);
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //链接状态绘制静止的圆和赛贝尔曲线
        if (mState == BUBBLE_STATE_CLICK) {
            //绘制静止的圆
            canvas.drawCircle(mQuitPoint!!.x, mQuitPoint!!.y, mQuitRadius, mBubblePaint!!)
            //绘制贝塞尔曲线
            //找到控制点
            val controlX = (mMovePoint!!.x + mQuitPoint!!.x) / 2
            val controlY = (mMovePoint!!.y + mQuitPoint!!.y) / 2
            //计算角度 在直角三角形中,非直角的sin值等于对边长比斜边长.使用勾股定理计算即可
            //sinA=对边/斜边  cosB=邻边/斜边   tanA=对边/邻边
            val sinTheta = (mMovePoint!!.y - mQuitPoint!!.y) / mDist
            val cosTheta = (mMovePoint!!.x - mQuitPoint!!.x) / mDist

            //A点
            val ax = mQuitPoint!!.x - mQuitRadius * sinTheta
            val ay = mQuitPoint!!.y + mQuitRadius * cosTheta
            //B点
            val bx = mMovePoint!!.x - mMoveRadius * sinTheta
            val by = mMovePoint!!.y + mMoveRadius * cosTheta
            //C点
            val cx = mMovePoint!!.x + mMoveRadius * sinTheta
            val cy = mMovePoint!!.y - mMoveRadius * cosTheta
            //D点
            val dx = mQuitPoint!!.x + mQuitRadius * sinTheta
            val dy = mQuitPoint!!.y - mQuitRadius * cosTheta

            //设置path的路径
            mBezierPath!!.reset()
            mBezierPath!!.moveTo(ax, ay)
            mBezierPath!!.quadTo(controlX, controlY, bx, by)
            mBezierPath!!.lineTo(cx, cy)
            mBezierPath!!.quadTo(controlX, controlY, dx, dy)
            mBezierPath!!.close()
            canvas.drawPath(mBezierPath!!, mBubblePaint!!)
        }

        //只要不是爆炸的情况都要绘制圆和字
        if (mState != BUBBLE_STATE_BLAST) {
            canvas.drawCircle(mMovePoint!!.x, mMovePoint!!.y, mMoveRadius, mBubblePaint!!)
            mTextPaint!!.getTextBounds(mText, 0, mText.length, mTextRect)
            canvas.drawText(
                mText,
                mMovePoint!!.x - mTextRect!!.width() / 2,
                mMovePoint!!.y + mTextRect!!.height() / 2,
                mTextPaint!!
            )
        }

        //爆炸状态绘制爆炸图片
        if (mState == BUBBLE_STATE_BLAST && mBlastIndex < mBlastDrawablesArray.size) {
            mBlastRect!!.left = mMovePoint!!.x - mMoveRadius
            mBlastRect!!.top = mMovePoint!!.y - mMoveRadius
            mBlastRect!!.right = mMovePoint!!.x + mMoveRadius
            mBlastRect!!.bottom = mMovePoint!!.y + mMoveRadius
            canvas.drawBitmap(mBlastBitmapsArray[mBlastIndex]!!, null, mBlastRect!!, mBlastPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //勾股定理算出点击位置和静止圆的圆心距离
                mDist = Math.hypot((x - mQuitPoint!!.x).toDouble(), (y - mQuitPoint!!.y).toDouble()).toFloat()
                if (mState == BUBBLE_STATE_DEFAULT) {
                    //如果手指点击到了圆上或者圆的附近
                    if (mDist < mMoveRadius + MOVE_OFFSET) {
                        mState = BUBBLE_STATE_CLICK
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mState != BUBBLE_STATE_DEFAULT) {
                    //勾股定理算出点击位置和静止圆的圆心距离,也就是手指一动的距离
                    mDist = Math.hypot((x - mQuitPoint!!.x).toDouble(), (y - mQuitPoint!!.y).toDouble()).toFloat()
                    mMovePoint!!.x = event.rawX
                    mMovePoint!!.y = event.rawY
                    //如果手指点击到了圆上或者圆的附近
                    if (mState == BUBBLE_STATE_CLICK) {
                        //手指移动动的距离小于我们定义的一个最大的距离，就绘制贝塞尔曲线，反之就是分离状态
                        if (mDist < mMaxDist - MOVE_OFFSET) {
                            mQuitRadius = mMoveRadius - mDist / 8
                        } else {
                            mState = BUBBLE_STATE_BREAK
                        }
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP ->                 //如果还没断开直接返回原状
                if (mState == BUBBLE_STATE_CLICK) {
                    //执行回弹动画
                    startBackAnim()
                } else if (mState == BUBBLE_STATE_BREAK) {
                    //如果断开了，小球的位置移动到距离2倍移动小球的距离以内也返回原状
                    if (mDist < mMoveRadius * 2) {
                        //执行回弹动画
                        startBackAnim()
                    } else {
                        mState = BUBBLE_STATE_BLAST
                        //执行爆炸动画
                        startBlastAnim()
                    }
                }
            else -> {}
        }
        return true
    }

    /**
     * 爆炸动画
     */
    private fun startBlastAnim() {
        val animator: ValueAnimator = ValueAnimator.ofInt(0, 5)
        animator.duration = 500
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            mBlastIndex = animation.animatedValue as Int
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (mOnExecuteFinishListener != null) {
                    mOnExecuteFinishListener!!.onFinish(EXECUTE_STATE_BLAST)
                }
            }
        })
        animator.start()
    }

    /**
     * 回弹动画
     */
    private fun startBackAnim() {
        val start = PointF(mMovePoint!!.x, mMovePoint!!.y)
        val end = PointF(mQuitPoint!!.x, mQuitPoint!!.y)
        //系统的PointFEvaluator只能支持21以上的,编译不通过。所以自己弄了一个把它代码抄过来就行啦
        val animator: ValueAnimator = ValueAnimator.ofObject(PointFEvaluator(), start, end)
        animator.duration = 200
        animator.interpolator = OvershootInterpolator(5f)
        animator.addUpdateListener { animation ->
            mMovePoint = animation.animatedValue as PointF?
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mState = BUBBLE_STATE_DEFAULT
                if (mOnExecuteFinishListener != null) {
                    mOnExecuteFinishListener!!.onFinish(EXECUTE_STATE_BACK)
                }
            }
        })
        animator.start()
    }

    interface OnExecuteFinishListener {
        /**
         * 执行完成
         * @param type
         */
        fun onFinish(type: Int)
    }
}