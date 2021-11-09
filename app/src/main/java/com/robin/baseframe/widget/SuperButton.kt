package com.robin.baseframe.widget


import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.robin.baseframe.R
import com.robin.baseframe.widget.alpha.IAlphaViewHelper
import com.robin.baseframe.widget.alpha.XUIAlphaViewHelper

class SuperButton : AppCompatButton {
    private var mContext: Context? = null
    private val defaultColor = 0x20000000
    private val defaultSelectorColor = 0x20000000
    private var mSolidColor = 0
    private var selectorPressedColor = 0
    private var selectorDisableColor = 0
    private var selectorNormalColor = 0
    private var cornersRadius = 0f
    private var cornersTopLeftRadius = 0f
    private var cornersTopRightRadius = 0f
    private var cornersBottomLeftRadius = 0f
    private var cornersBottomRightRadius = 0f
    private var strokeWidth = 0
    private var strokeColor = 0
    private var strokeDashWidth = 0f
    private var strokeDashGap = 0f
    private var sizeWidth = 0
    private var sizeHeight = 0
    private var gradientOrientation = 0
    private var gradientAngle = 0
    private var gradientCenterX = 0
    private var gradientCenterY = 0
    private var gradientGradientRadius = 0
    private var gradientStartColor = 0
    private var gradientCenterColor = 0
    private var gradientEndColor = 0
    private var gradientType = 0
    private var gradientUseLevel = false
    private var useSelector = false
    private var shapeType = 0
    private var mGravity = 0
    private var gradientDrawable: GradientDrawable? = null

    constructor(context: Context) : super(context) {
        initAttrs(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        mContext = context
        getAttr(attrs)
        init()
    }

    private fun getAttr(attrs: AttributeSet?) {
        val typedArray = mContext!!.obtainStyledAttributes(attrs, R.styleable.SuperButton)
        mGravity = typedArray.getInt(R.styleable.SuperButton_sGravity, 0)
        shapeType =
            typedArray.getInt(R.styleable.SuperButton_sShapeType, GradientDrawable.RECTANGLE)
        mSolidColor = typedArray.getColor(R.styleable.SuperButton_sSolidColor, defaultColor)
        selectorPressedColor =
            typedArray.getColor(R.styleable.SuperButton_sSelectorPressedColor, defaultSelectorColor)
        selectorDisableColor =
            typedArray.getColor(R.styleable.SuperButton_sSelectorDisableColor, defaultSelectorColor)
        selectorNormalColor =
            typedArray.getColor(R.styleable.SuperButton_sSelectorNormalColor, defaultSelectorColor)
        cornersRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sCornersRadius, 0).toFloat()
        cornersTopLeftRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sCornersTopLeftRadius, 0)
                .toFloat()
        cornersTopRightRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sCornersTopRightRadius, 0)
                .toFloat()
        cornersBottomLeftRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sCornersBottomLeftRadius, 0)
                .toFloat()
        cornersBottomRightRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sCornersBottomRightRadius, 0)
                .toFloat()
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.SuperButton_sStrokeWidth, 0)
        strokeDashWidth =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sStrokeDashWidth, 0).toFloat()
        strokeDashGap =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sStrokeDashGap, 0).toFloat()
        strokeColor = typedArray.getColor(R.styleable.SuperButton_sStrokeColor, defaultColor)
        sizeWidth = typedArray.getDimensionPixelSize(R.styleable.SuperButton_sSizeWidth, 0)
        sizeHeight = typedArray.getDimensionPixelSize(
            R.styleable.SuperButton_sSizeHeight,
            dip2px(mContext, 48f)
        )
        gradientOrientation = typedArray.getInt(R.styleable.SuperButton_sGradientOrientation, -1)
        gradientAngle = typedArray.getDimensionPixelSize(R.styleable.SuperButton_sGradientAngle, 0)
        gradientCenterX =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sGradientCenterX, 0)
        gradientCenterY =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sGradientCenterY, 0)
        gradientGradientRadius =
            typedArray.getDimensionPixelSize(R.styleable.SuperButton_sGradientGradientRadius, 0)
        gradientStartColor = typedArray.getColor(R.styleable.SuperButton_sGradientStartColor, -1)
        gradientCenterColor = typedArray.getColor(R.styleable.SuperButton_sGradientCenterColor, -1)
        gradientEndColor = typedArray.getColor(R.styleable.SuperButton_sGradientEndColor, -1)
        gradientType = typedArray.getInt(R.styleable.SuperButton_sGradientType, linear)
        gradientUseLevel = typedArray.getBoolean(R.styleable.SuperButton_sGradientUseLevel, false)
        useSelector = typedArray.getBoolean(R.styleable.SuperButton_sUseSelector, false)
        typedArray.recycle()
    }

    private fun init() {
        isClickable = true
        if (Build.VERSION.SDK_INT < 16) {
            setBackgroundDrawable(if (useSelector) selector else getDrawable(0))
        } else {
            background = if (useSelector) selector else getDrawable(0)
        }
        setSGravity()
    }//注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
    //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
    /**
     * 获取设置之后的Selector
     *
     * @return stateListDrawable
     */
    val selector: StateListDrawable
        get() {
            val stateListDrawable = StateListDrawable()

            //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
            //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
            stateListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), getDrawable(
                    android.R.attr.state_pressed
                )
            )
            stateListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                getDrawable(-android.R.attr.state_enabled)
            )
            stateListDrawable.addState(intArrayOf(), getDrawable(android.R.attr.state_enabled))
            return stateListDrawable
        }

    /**
     * 设置GradientDrawable
     *
     * @param state 按钮状态
     * @return gradientDrawable
     */
    fun getDrawable(state: Int): GradientDrawable {
        gradientDrawable = GradientDrawable()
        setShape()
        setOrientation()
        setSize()
        setBorder()
        setRadius()
        setSelectorColor(state)
        return gradientDrawable!!
    }

    /**
     * 设置文字对其方式
     */
    private fun setSGravity() {
        when (mGravity) {
            0 -> setGravity(Gravity.CENTER)
            1 -> setGravity(Gravity.START or Gravity.CENTER_VERTICAL)
            2 -> setGravity(Gravity.END or Gravity.CENTER_VERTICAL)
            3 -> setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            4 -> setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        }
    }

    /**
     * 设置Selector的不同状态的颜色
     *
     * @param state 按钮状态
     */
    private fun setSelectorColor(state: Int) {
        if (gradientOrientation == -1) {
            when (state) {
                android.R.attr.state_pressed -> gradientDrawable!!.setColor(selectorPressedColor)
                -android.R.attr.state_enabled -> gradientDrawable!!.setColor(selectorDisableColor)
                android.R.attr.state_enabled -> gradientDrawable!!.setColor(selectorNormalColor)
            }
        }
    }

    /**
     * 设置背景颜色
     * 如果设定的有Orientation 就默认为是渐变色的Button，否则就是纯色的Button
     */
    private fun setOrientation() {
        if (isUseGradientColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                gradientDrawable!!.orientation = getOrientation(gradientOrientation)
                if (gradientCenterColor == -1) {
                    gradientDrawable!!.colors = intArrayOf(gradientStartColor, gradientEndColor)
                } else {
                    gradientDrawable!!.colors =
                        intArrayOf(gradientStartColor, gradientCenterColor, gradientEndColor)
                }
                when (gradientType) {
                    linear -> gradientDrawable!!.gradientType =
                        GradientDrawable.LINEAR_GRADIENT
                    radial -> {
                        gradientDrawable!!.gradientType = GradientDrawable.RADIAL_GRADIENT
                        gradientDrawable!!.gradientRadius = gradientGradientRadius.toFloat()
                    }
                    sweep -> gradientDrawable!!.gradientType =
                        GradientDrawable.SWEEP_GRADIENT
                }
                gradientDrawable!!.useLevel = gradientUseLevel
                if (gradientCenterX != 0 && gradientCenterY != 0) {
                    gradientDrawable!!.setGradientCenter(
                        gradientCenterX.toFloat(),
                        gradientCenterY.toFloat()
                    )
                }
            }
        } else {
            gradientDrawable!!.setColor(mSolidColor)
        }
    }

    /**
     * 设置颜色渐变类型
     *
     * @param gradientOrientation gradientOrientation
     * @return Orientation
     */
    private fun getOrientation(gradientOrientation: Int): GradientDrawable.Orientation? {
        var orientation: GradientDrawable.Orientation? = null
        when (gradientOrientation) {
            TOP_BOTTOM -> orientation = GradientDrawable.Orientation.TOP_BOTTOM
            TR_BL -> orientation = GradientDrawable.Orientation.TR_BL
            RIGHT_LEFT -> orientation = GradientDrawable.Orientation.RIGHT_LEFT
            BR_TL -> orientation = GradientDrawable.Orientation.BR_TL
            BOTTOM_TOP -> orientation = GradientDrawable.Orientation.BOTTOM_TOP
            BL_TR -> orientation = GradientDrawable.Orientation.BL_TR
            LEFT_RIGHT -> orientation = GradientDrawable.Orientation.LEFT_RIGHT
            TL_BR -> orientation = GradientDrawable.Orientation.TL_BR
        }
        return orientation
    }

    /**
     * 设置shape类型
     */
    private fun setShape() {
        when (shapeType) {
            RECTANGLE -> gradientDrawable!!.shape =
                GradientDrawable.RECTANGLE
            OVAL -> gradientDrawable!!.shape = GradientDrawable.OVAL
            LINE -> gradientDrawable!!.shape = GradientDrawable.LINE
            RING -> gradientDrawable!!.shape = GradientDrawable.RING
        }
    }

    private fun setSize() {
        if (shapeType == RECTANGLE) {
            gradientDrawable!!.setSize(sizeWidth, sizeHeight)
        }
    }

    /**
     * 设置边框  宽度  颜色  虚线  间隙
     */
    private fun setBorder() {
        gradientDrawable!!.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap)
    }

    /**
     * 只有类型是矩形的时候设置圆角半径才有效
     */
    private fun setRadius() {
        if (shapeType == GradientDrawable.RECTANGLE) {
            if (cornersRadius != 0f) {
                gradientDrawable!!.cornerRadius = cornersRadius //设置圆角的半径
            } else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gradientDrawable!!.cornerRadii = floatArrayOf(
                    cornersTopLeftRadius, cornersTopLeftRadius,
                    cornersTopRightRadius, cornersTopRightRadius,
                    cornersBottomRightRadius, cornersBottomRightRadius,
                    cornersBottomLeftRadius, cornersBottomLeftRadius
                )
            }
        }
    }
    /////////////////对外暴露的方法//////////////
    /**
     * 设置Shape类型
     *
     * @param type 类型
     * @return 对象
     */
    fun setShapeType(type: Int): SuperButton {
        shapeType = type
        return this
    }

    /**
     * 设置文字对其方式
     *
     * @param gravity 对齐方式
     * @return 对象
     */
    fun setTextGravity(gravity: Int): SuperButton {
        this.mGravity = gravity
        return this
    }

    /**
     * 设置按下的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    fun setShapeSelectorPressedColor(color: Int): SuperButton {
        selectorPressedColor = color
        return this
    }

    /**
     * 设置正常的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    fun setShapeSelectorNormalColor(color: Int): SuperButton {
        selectorNormalColor = color
        return this
    }

    /**
     * 设置不可点击的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    fun setShapeSelectorDisableColor(color: Int): SuperButton {
        selectorDisableColor = color
        return this
    }

    /**
     * 设置填充的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    fun setShapeSolidColor(color: Int): SuperButton {
        mSolidColor = color
        return this
    }

    /**
     * 设置边框宽度
     *
     * @param strokeWidth 边框宽度值
     * @return 对象
     */
    fun setShapeStrokeWidth(strokeWidth: Int): SuperButton {
        this.strokeWidth = dip2px(mContext, strokeWidth.toFloat())
        return this
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor 边框颜色
     * @return 对象
     */
    fun setShapeStrokeColor(strokeColor: Int): SuperButton {
        this.strokeColor = strokeColor
        return this
    }

    /**
     * 设置边框虚线宽度
     *
     * @param strokeDashWidth 边框虚线宽度
     * @return 对象
     */
    fun setShapeStrokeDashWidth(strokeDashWidth: Float): SuperButton {
        this.strokeDashWidth = dip2px(mContext, strokeDashWidth).toFloat()
        return this
    }

    /**
     * 设置边框虚线间隙
     *
     * @param strokeDashGap 边框虚线间隙值
     * @return 对象
     */
    fun setShapeStrokeDashGap(strokeDashGap: Float): SuperButton {
        this.strokeDashGap = dip2px(mContext, strokeDashGap).toFloat()
        return this
    }

    /**
     * 设置圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    fun setShapeCornersRadius(radius: Float): SuperButton {
        cornersRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * 设置左上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    fun setShapeCornersTopLeftRadius(radius: Float): SuperButton {
        cornersTopLeftRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * 设置右上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    fun setShapeCornersTopRightRadius(radius: Float): SuperButton {
        cornersTopRightRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * 设置左下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    fun setShapeCornersBottomLeftRadius(radius: Float): SuperButton {
        cornersBottomLeftRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * 设置右下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    fun setShapeCornersBottomRightRadius(radius: Float): SuperButton {
        cornersBottomRightRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * 设置shape的宽度
     *
     * @param sizeWidth 宽
     * @return 对象
     */
    fun setShapeSizeWidth(sizeWidth: Int): SuperButton {
        this.sizeWidth = sizeWidth
        return this
    }

    /**
     * 设置shape的高度
     *
     * @param sizeHeight 高
     * @return 对象
     */
    fun setShapeSizeHeight(sizeHeight: Int): SuperButton {
        this.sizeHeight = sizeHeight
        return this
    }

    /**
     * 设置背景渐变方式
     *
     * @param gradientOrientation 渐变类型
     * @return 对象
     */
    fun setShapeGradientOrientation(gradientOrientation: Int): SuperButton {
        this.gradientOrientation = gradientOrientation
        return this
    }

    /**
     * 设置渐变中心X
     *
     * @param gradientCenterX 中心x
     * @return 对象
     */
    fun setShapeGradientCenterX(gradientCenterX: Int): SuperButton {
        this.gradientCenterX = gradientCenterX
        return this
    }

    /**
     * 设置渐变中心Y
     *
     * @param gradientCenterY 中心y
     * @return 对象
     */
    fun setShapeGradientCenterY(gradientCenterY: Int): SuperButton {
        this.gradientCenterY = gradientCenterY
        return this
    }

    /**
     * 设置渐变半径
     *
     * @param gradientGradientRadius 渐变半径
     * @return 对象
     */
    fun setShapeGradientGradientRadius(gradientGradientRadius: Int): SuperButton {
        this.gradientGradientRadius = gradientGradientRadius
        return this
    }

    /**
     * 设置渐变开始的颜色
     *
     * @param gradientStartColor 开始颜色
     * @return 对象
     */
    fun setShapeGradientStartColor(gradientStartColor: Int): SuperButton {
        this.gradientStartColor = gradientStartColor
        return this
    }

    /**
     * 设置渐变中间的颜色
     *
     * @param gradientCenterColor 中间颜色
     * @return 对象
     */
    fun setShapeGradientCenterColor(gradientCenterColor: Int): SuperButton {
        this.gradientCenterColor = gradientCenterColor
        return this
    }

    /**
     * 设置渐变结束的颜色
     *
     * @param gradientEndColor 结束颜色
     * @return 对象
     */
    fun setShapeGradientEndColor(gradientEndColor: Int): SuperButton {
        this.gradientEndColor = gradientEndColor
        return this
    }

    /**
     * 设置渐变类型
     *
     * @param gradientType 类型
     * @return 对象
     */
    fun setShapeGradientType(gradientType: Int): SuperButton {
        this.gradientType = gradientType
        return this
    }

    /**
     * 设置是否使用UseLevel
     *
     * @param gradientUseLevel true  or  false
     * @return 对象
     */
    fun setShapeGradientUseLevel(gradientUseLevel: Boolean): SuperButton {
        this.gradientUseLevel = gradientUseLevel
        return this
    }

    /**
     * 是否使用selector
     *
     * @param useSelector true  or  false
     * @return 对象
     */
    fun setShapeUseSelector(useSelector: Boolean): SuperButton {
        this.useSelector = useSelector
        return this
    }

    /**
     * 使用shape
     * 所有与shape相关的属性设置之后调用此方法才生效
     */
    fun setUseShape() {
        init()
    }

    /**
     * 单位转换工具类
     *
     * @param context  上下文对象
     * @param dipValue 值
     * @return 返回值
     */
    private fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    private var mAlphaViewHelper: IAlphaViewHelper? = null
    private val alphaViewHelper: IAlphaViewHelper?
        private get() {
            if (mAlphaViewHelper == null) {
                mAlphaViewHelper =
                    XUIAlphaViewHelper(this)
            }
            return mAlphaViewHelper
        }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.onPressedChanged(this, pressed)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.onEnabledChanged(this, enabled)
        }
    }

    private val isEnableChangeAlpha: Boolean
        private get() = isUseGradientColor || selectorNormalColor == selectorPressedColor

    /**
     * 是否使用渐变色
     *
     * @return
     */
    private val isUseGradientColor: Boolean
        private get() = gradientOrientation != -1

    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param changeAlphaWhenPress 是否要在 press 时改变透明度
     */
    fun setChangeAlphaWhenPress(changeAlphaWhenPress: Boolean) {
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.setChangeAlphaWhenPress(changeAlphaWhenPress)
        }
    }

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param changeAlphaWhenDisable 是否要在 disabled 时改变透明度
     */
    fun setChangeAlphaWhenDisable(changeAlphaWhenDisable: Boolean) {
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.setChangeAlphaWhenDisable(changeAlphaWhenDisable)
        }
    }

    companion object {
        //"linear"	线形渐变。这也是默认的模式
        private const val linear = 0

        //"radial"	辐射渐变。startColor即辐射中心的颜色
        private const val radial = 1

        //"sweep"	扫描线渐变。
        private const val sweep = 2

        //shape的样式
        const val RECTANGLE = 0
        const val OVAL = 1
        const val LINE = 2
        const val RING = 3

        //渐变色的显示方式
        const val TOP_BOTTOM = 0
        const val TR_BL = 1
        const val RIGHT_LEFT = 2
        const val BR_TL = 3
        const val BOTTOM_TOP = 4
        const val BL_TR = 5
        const val LEFT_RIGHT = 6
        const val TL_BR = 7

        //文字显示的位置方式
        const val TEXT_GRAVITY_CENTER = 0
        const val TEXT_GRAVITY_LEFT = 1
        const val TEXT_GRAVITY_RIGHT = 2
        const val TEXT_GRAVITY_TOP = 3
        const val TEXT_GRAVITY_BOTTOM = 4
    }
}