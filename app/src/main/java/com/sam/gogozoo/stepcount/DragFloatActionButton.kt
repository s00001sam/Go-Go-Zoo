package com.sam.gogozoo.stepcount

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.sqrt

class DragFloatActionButton : ConstraintLayout {

    private var parentHeight: Int = 0
    private var parentWidth: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var lastX: Int = 0
    private var lastY: Int = 0

    private var isDrag: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                isDrag = false
                parent.requestDisallowInterceptTouchEvent(true)
                lastX = rawX
                lastY = rawY
                val parent: ViewGroup
                if (getParent() != null) {
                    parent = getParent() as ViewGroup
                    parentHeight = parent.height
                    parentWidth = parent.width
                }
            }
            MotionEvent.ACTION_MOVE -> {
                isDrag = !(parentHeight <= 0 || parentWidth === 0)
                val dx = rawX - lastX
                val dy = rawY - lastY
                val distance = sqrt((dx * dx + dy * dy).toDouble()).toInt()
                if (distance == 0) {
                    isDrag = false
                } else {
                    var x = x + dx
                    var y = y + dy
                    x =
                        if (x < 0) 0F else if (x > parentWidth - width) (parentWidth - width).toFloat() else x
                    y =
                        if (getY() < 0) 0F else if (getY() + height > parentHeight) (parentHeight - height).toFloat() else y
                    setX(x)
                    setY(y)
                    lastX = rawX
                    lastY = rawY
                    Log.w(
                        "Connie",
                        "isDrag=" + isDrag + "getX=" + getX() + ";getY=" + getY() + ";parentWidth=" + parentWidth
                    )
                }
            }
            MotionEvent.ACTION_UP -> if (!isNotDrag()) {
                isPressed = false
                if (rawX >= parentWidth / 2) {
                    animate().setInterpolator(DecelerateInterpolator())
                        .setDuration(500)
                        .xBy(parentWidth - width - x)
                        .start()
                } else {
                    val oa = ObjectAnimator.ofFloat(this, "x", x, 0F)
                    oa.interpolator = DecelerateInterpolator()
                    oa.duration = 500
                    oa.start()
                }
            }
        }
        return !isNotDrag() || super.onTouchEvent(event)
    }

    private fun isNotDrag(): Boolean {
        return !isDrag && (x == 0f || x == (parentWidth - width).toFloat())
    }
}