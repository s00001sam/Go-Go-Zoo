package com.sam.gogozoo

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null):
    AppCompatTextView(context, attrs) {
    init{
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isFocusable = true
        marqueeRepeatLimit = -1
        isFocusableInTouchMode = true
        setHorizontallyScrolling(true)
    }

    override fun isFocused(): Boolean {
        return true
    }
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if(focused) super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if(hasWindowFocus) super.onWindowFocusChanged(hasWindowFocus)
    }
}