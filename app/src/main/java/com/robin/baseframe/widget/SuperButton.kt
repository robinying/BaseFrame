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
    }//?????????????????????????????????????????????????????????????????????????????????
    //????????????????????????????????????????????????sd.addState(new[]{},normal)????????????????????????????????????????????????
    /**
     * ?????????????????????Selector
     *
     * @return stateListDrawable
     */
    val selector: StateListDrawable
        get() {
            val stateListDrawable = StateListDrawable()

            //?????????????????????????????????????????????????????????????????????????????????
            //????????????????????????????????????????????????sd.addState(new[]{},normal)????????????????????????????????????????????????
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
     * ??????GradientDrawable
     *
     * @param state ????????????
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
     * ????????????????????????
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
     * ??????Selector????????????????????????
     *
     * @param state ????????????
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
     * ??????????????????
     * ??????????????????Orientation ???????????????????????????Button????????????????????????Button
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
     * ????????????????????????
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
     * ??????shape??????
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
     * ????????????  ??????  ??????  ??????  ??????
     */
    private fun setBorder() {
        gradientDrawable!!.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap)
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private fun setRadius() {
        if (shapeType == GradientDrawable.RECTANGLE) {
            if (cornersRadius != 0f) {
                gradientDrawable!!.cornerRadius = cornersRadius //?????????????????????
            } else {
                //1???2??????????????????????????????3???4??????????????????5???6??????????????????7???8???????????????
                gradientDrawable!!.cornerRadii = floatArrayOf(
                    cornersTopLeftRadius, cornersTopLeftRadius,
                    cornersTopRightRadius, cornersTopRightRadius,
                    cornersBottomRightRadius, cornersBottomRightRadius,
                    cornersBottomLeftRadius, cornersBottomLeftRadius
                )
            }
        }
    }
    /////////////////?????????????????????//////////////
    /**
     * ??????Shape??????
     *
     * @param type ??????
     * @return ??????
     */
    fun setShapeType(type: Int): SuperButton {
        shapeType = type
        return this
    }

    /**
     * ????????????????????????
     *
     * @param gravity ????????????
     * @return ??????
     */
    fun setTextGravity(gravity: Int): SuperButton {
        this.mGravity = gravity
        return this
    }

    /**
     * ?????????????????????
     *
     * @param color ??????
     * @return ??????
     */
    fun setShapeSelectorPressedColor(color: Int): SuperButton {
        selectorPressedColor = color
        return this
    }

    /**
     * ?????????????????????
     *
     * @param color ??????
     * @return ??????
     */
    fun setShapeSelectorNormalColor(color: Int): SuperButton {
        selectorNormalColor = color
        return this
    }

    /**
     * ???????????????????????????
     *
     * @param color ??????
     * @return ??????
     */
    fun setShapeSelectorDisableColor(color: Int): SuperButton {
        selectorDisableColor = color
        return this
    }

    /**
     * ?????????????????????
     *
     * @param color ??????
     * @return ??????
     */
    fun setShapeSolidColor(color: Int): SuperButton {
        mSolidColor = color
        return this
    }

    /**
     * ??????????????????
     *
     * @param strokeWidth ???????????????
     * @return ??????
     */
    fun setShapeStrokeWidth(strokeWidth: Int): SuperButton {
        this.strokeWidth = dip2px(mContext, strokeWidth.toFloat())
        return this
    }

    /**
     * ??????????????????
     *
     * @param strokeColor ????????????
     * @return ??????
     */
    fun setShapeStrokeColor(strokeColor: Int): SuperButton {
        this.strokeColor = strokeColor
        return this
    }

    /**
     * ????????????????????????
     *
     * @param strokeDashWidth ??????????????????
     * @return ??????
     */
    fun setShapeStrokeDashWidth(strokeDashWidth: Float): SuperButton {
        this.strokeDashWidth = dip2px(mContext, strokeDashWidth).toFloat()
        return this
    }

    /**
     * ????????????????????????
     *
     * @param strokeDashGap ?????????????????????
     * @return ??????
     */
    fun setShapeStrokeDashGap(strokeDashGap: Float): SuperButton {
        this.strokeDashGap = dip2px(mContext, strokeDashGap).toFloat()
        return this
    }

    /**
     * ??????????????????
     *
     * @param radius ??????
     * @return ??????
     */
    fun setShapeCornersRadius(radius: Float): SuperButton {
        cornersRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * ????????????????????????
     *
     * @param radius ??????
     * @return ??????
     */
    fun setShapeCornersTopLeftRadius(radius: Float): SuperButton {
        cornersTopLeftRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * ????????????????????????
     *
     * @param radius ??????
     * @return ??????
     */
    fun setShapeCornersTopRightRadius(radius: Float): SuperButton {
        cornersTopRightRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * ????????????????????????
     *
     * @param radius ??????
     * @return ??????
     */
    fun setShapeCornersBottomLeftRadius(radius: Float): SuperButton {
        cornersBottomLeftRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * ????????????????????????
     *
     * @param radius ??????
     * @return ??????
     */
    fun setShapeCornersBottomRightRadius(radius: Float): SuperButton {
        cornersBottomRightRadius = dip2px(mContext, radius).toFloat()
        return this
    }

    /**
     * ??????shape?????????
     *
     * @param sizeWidth ???
     * @return ??????
     */
    fun setShapeSizeWidth(sizeWidth: Int): SuperButton {
        this.sizeWidth = sizeWidth
        return this
    }

    /**
     * ??????shape?????????
     *
     * @param sizeHeight ???
     * @return ??????
     */
    fun setShapeSizeHeight(sizeHeight: Int): SuperButton {
        this.sizeHeight = sizeHeight
        return this
    }

    /**
     * ????????????????????????
     *
     * @param gradientOrientation ????????????
     * @return ??????
     */
    fun setShapeGradientOrientation(gradientOrientation: Int): SuperButton {
        this.gradientOrientation = gradientOrientation
        return this
    }

    /**
     * ??????????????????X
     *
     * @param gradientCenterX ??????x
     * @return ??????
     */
    fun setShapeGradientCenterX(gradientCenterX: Int): SuperButton {
        this.gradientCenterX = gradientCenterX
        return this
    }

    /**
     * ??????????????????Y
     *
     * @param gradientCenterY ??????y
     * @return ??????
     */
    fun setShapeGradientCenterY(gradientCenterY: Int): SuperButton {
        this.gradientCenterY = gradientCenterY
        return this
    }

    /**
     * ??????????????????
     *
     * @param gradientGradientRadius ????????????
     * @return ??????
     */
    fun setShapeGradientGradientRadius(gradientGradientRadius: Int): SuperButton {
        this.gradientGradientRadius = gradientGradientRadius
        return this
    }

    /**
     * ???????????????????????????
     *
     * @param gradientStartColor ????????????
     * @return ??????
     */
    fun setShapeGradientStartColor(gradientStartColor: Int): SuperButton {
        this.gradientStartColor = gradientStartColor
        return this
    }

    /**
     * ???????????????????????????
     *
     * @param gradientCenterColor ????????????
     * @return ??????
     */
    fun setShapeGradientCenterColor(gradientCenterColor: Int): SuperButton {
        this.gradientCenterColor = gradientCenterColor
        return this
    }

    /**
     * ???????????????????????????
     *
     * @param gradientEndColor ????????????
     * @return ??????
     */
    fun setShapeGradientEndColor(gradientEndColor: Int): SuperButton {
        this.gradientEndColor = gradientEndColor
        return this
    }

    /**
     * ??????????????????
     *
     * @param gradientType ??????
     * @return ??????
     */
    fun setShapeGradientType(gradientType: Int): SuperButton {
        this.gradientType = gradientType
        return this
    }

    /**
     * ??????????????????UseLevel
     *
     * @param gradientUseLevel true  or  false
     * @return ??????
     */
    fun setShapeGradientUseLevel(gradientUseLevel: Boolean): SuperButton {
        this.gradientUseLevel = gradientUseLevel
        return this
    }

    /**
     * ????????????selector
     *
     * @param useSelector true  or  false
     * @return ??????
     */
    fun setShapeUseSelector(useSelector: Boolean): SuperButton {
        this.useSelector = useSelector
        return this
    }

    /**
     * ??????shape
     * ?????????shape???????????????????????????????????????????????????
     */
    fun setUseShape() {
        init()
    }

    /**
     * ?????????????????????
     *
     * @param context  ???????????????
     * @param dipValue ???
     * @return ?????????
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
     * ?????????????????????
     *
     * @return
     */
    private val isUseGradientColor: Boolean
        private get() = gradientOrientation != -1

    /**
     * ?????????????????? press ??????????????????
     *
     * @param changeAlphaWhenPress ???????????? press ??????????????????
     */
    fun setChangeAlphaWhenPress(changeAlphaWhenPress: Boolean) {
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.setChangeAlphaWhenPress(changeAlphaWhenPress)
        }
    }

    /**
     * ?????????????????? disabled ??????????????????
     *
     * @param changeAlphaWhenDisable ???????????? disabled ??????????????????
     */
    fun setChangeAlphaWhenDisable(changeAlphaWhenDisable: Boolean) {
        if (isEnableChangeAlpha) {
            alphaViewHelper!!.setChangeAlphaWhenDisable(changeAlphaWhenDisable)
        }
    }

    companion object {
        //"linear"	???????????????????????????????????????
        private const val linear = 0

        //"radial"	???????????????startColor????????????????????????
        private const val radial = 1

        //"sweep"	??????????????????
        private const val sweep = 2

        //shape?????????
        const val RECTANGLE = 0
        const val OVAL = 1
        const val LINE = 2
        const val RING = 3

        //????????????????????????
        const val TOP_BOTTOM = 0
        const val TR_BL = 1
        const val RIGHT_LEFT = 2
        const val BR_TL = 3
        const val BOTTOM_TOP = 4
        const val BL_TR = 5
        const val LEFT_RIGHT = 6
        const val TL_BR = 7

        //???????????????????????????
        const val TEXT_GRAVITY_CENTER = 0
        const val TEXT_GRAVITY_LEFT = 1
        const val TEXT_GRAVITY_RIGHT = 2
        const val TEXT_GRAVITY_TOP = 3
        const val TEXT_GRAVITY_BOTTOM = 4
    }
}