package uk.akane.accord.ui.components.lyrics

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import uk.akane.accord.logic.dp
import androidx.core.view.isNotEmpty
import uk.akane.accord.logic.forEachChild
import kotlin.math.roundToInt

class LyricsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    val contentPaddingTop = 160.dp.px.roundToInt()

    fun update(lyrics: Lyrics) {
        forEachChild { child ->
            child as LyricsLineView
            child.release()
        }
        removeAllViews()
        val deviceHeight = resources.displayMetrics.heightPixels.toFloat()
        lyrics.lyrics.forEachIndexed { index, line ->
            val view = LyricsLineView(context, line)
            view.setAnimations(index, 0f, deviceHeight)
            addView(view)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

        var layoutHeight = contentPaddingTop
        forEachChild { child ->
            child.measure(widthMeasureSpec, heightMeasureSpec)
            child as LyricsLineView
            child.animations.setGlobalOffset(layoutHeight.toFloat())
            layoutHeight += child.measuredHeight
        }
        if (isNotEmpty()) {
            val lastChild = getChildAt(childCount - 1)
            layoutHeight += rootView.height - contentPaddingTop - lastChild.measuredHeight
        }

        setMeasuredDimension(width, layoutHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = contentPaddingTop
        forEachChild { child ->
            val height = child.measuredHeight
            child.layout(0, top, child.measuredWidth, top + height)
            top += height
        }
    }
}