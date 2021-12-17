package com.niimbot.jcgqsh.view.widget.search

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.math.max

/**
 * 流式布局，当行宽不够时换行
 * @Author wuchao
 * @Date 2020/9/2-12:00 PM
 * @see
 * @description
 * @email 329187218@qq.com
 */
open class HorizontalFlowLayout : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        val paddingHorizontal = paddingLeft + paddingRight
        val paddingVertical = paddingTop + paddingBottom
        var widthUsed = 0
        var heightUsed = 0
        var fixedHeightUsed = 0
        var maxUsedLineWidth = 0
        var currentLineChildMaxHeight = heightUsed
        for (index in 0 until count) {
            val child = getChildAt(index)
            val childLayoutParams = child.layoutParams as MarginLayoutParams
            val childWidthMeasureSpec = getChildMeasureSpec(
                widthMeasureSpec,
                child.paddingLeft + child.paddingRight + childLayoutParams.leftMargin + childLayoutParams.rightMargin,
                childLayoutParams.width
            )
            val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                heightMeasureSpec,
                child.paddingTop + child.paddingBottom + childLayoutParams.topMargin + childLayoutParams.bottomMargin + heightUsed,
                childLayoutParams.height
            )
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            val preMeasureWidth = child.measuredWidth
            if (widthUsed + preMeasureWidth + paddingHorizontal > (View.MEASURED_SIZE_MASK and resolveSizeAndState(
                    widthUsed + preMeasureWidth + paddingHorizontal,
                    widthMeasureSpec,
                    0
                ))
            ) {
                //change line
                fixedHeightUsed += heightUsed
                currentLineChildMaxHeight = 0
                widthUsed = 0
            }
            widthUsed += child.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            maxUsedLineWidth = max(widthUsed, maxUsedLineWidth)
            currentLineChildMaxHeight = max(
                child.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin,
                currentLineChildMaxHeight
            )
            heightUsed = fixedHeightUsed + currentLineChildMaxHeight
        }
        maxUsedLineWidth += paddingHorizontal
        heightUsed += paddingVertical
        setMeasuredDimension(maxUsedLineWidth, heightUsed)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var horizontalSpaceUsed = paddingLeft
        var verticalSpaceUsed = paddingTop
        var currentLineMaxHeight = 0
        for (index in 0 until count) {
            val child = getChildAt(index)
            val childLayoutParams = child.layoutParams as MarginLayoutParams
            val childMarginVertical = childLayoutParams.topMargin + childLayoutParams.bottomMargin
            var childLeft = horizontalSpaceUsed + childLayoutParams.leftMargin
            var childTop = verticalSpaceUsed + childLayoutParams.topMargin
            var childRight = childLeft + child.measuredWidth
            var childBottom = childTop + child.measuredHeight
            if (childRight + childLayoutParams.rightMargin + paddingRight > r) {
                //change line
                horizontalSpaceUsed = paddingLeft
                verticalSpaceUsed += currentLineMaxHeight
                currentLineMaxHeight = 0
                childLeft = horizontalSpaceUsed + childLayoutParams.leftMargin
                childTop = verticalSpaceUsed + childLayoutParams.topMargin
                childRight = childLeft + child.measuredWidth
                childBottom = childTop + child.measuredHeight
            } else {
                currentLineMaxHeight =
                    max(child.measuredHeight + childMarginVertical, currentLineMaxHeight)
            }
            child.layout(childLeft, childTop, childRight, childBottom)
            horizontalSpaceUsed = childRight + childLayoutParams.rightMargin
        }
    }


}