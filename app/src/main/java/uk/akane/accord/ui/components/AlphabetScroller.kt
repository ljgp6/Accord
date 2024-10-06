package uk.akane.accord.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import uk.akane.accord.R

class AlphabetScroller @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val letters = ('A'..'Z') + '#'
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimensionPixelSize(R.dimen.scroller_char_size).toFloat()
        color = resources.getColor(R.color.accentColor, null)
        typeface = ResourcesCompat.getFont(context, R.font.inter_semibold)
    }

    private val scrollerWidth = resources.getDimensionPixelSize(R.dimen.scroller_width)
    private val fatWidth = paint.measureText("W")

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            scrollerWidth,
            MeasureSpec.makeMeasureSpec((letters.size * (paint.textSize + 10)).toInt(), MeasureSpec.EXACTLY) // 文字高度 + 间距
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var y = paint.textSize + 5

        letters.forEach { letter ->
            canvas.drawText(
                letter.toString(),
                width - fatWidth + (fatWidth - paint.measureText(letter.toString())) / 2,
                y,
                paint
            )
            y += paint.textSize + 10
        }
    }
}
