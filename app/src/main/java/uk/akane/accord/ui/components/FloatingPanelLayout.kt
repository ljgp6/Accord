package uk.akane.accord.ui.components

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowInsets
import android.view.animation.PathInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.window.layout.WindowMetricsCalculator
import uk.akane.accord.R
import uk.akane.accord.logic.dpToPx
import uk.akane.accord.logic.scale
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


class FloatingPanelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes),
    GestureDetector.OnGestureListener {

    companion object {
        const val TAG = "FloatingPanelLayout"
    }

    private val windowHeight: Int
        get() = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context).bounds.height()

    private var valueAnimator: ValueAnimator? = null

    private var gestureDetector = GestureDetector(context, this)

    private var progress: Float = 0f

    private var initialMargin = IntArray(4)
    private var initialHeight = 0
    private var instanceHeight = 0
    private var instanceBottomMargin = 0

    private var scrollHeight = 0
    private var scrollBottomMargin = 0

    private var isScrollingUp = false

    private var retractThreshold = 0.1F

    private var defaultMaxDuration = 350L
    private var defaultMinDuration = 220L
    private var defaultUpActionDuration = 285L
    private var defaultInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)

    enum class SlideStatus {
        COLLAPSED, EXPANDED, SLIDING
    }

    interface OnSlideListener {
        fun onSlideStatusChanged(status: SlideStatus)

        fun onSlide(value: Float)
    }

    var state: SlideStatus = SlideStatus.COLLAPSED
    var onSlideListener: OnSlideListener? = null

    inner class OutlineProvider(
        private val rect: Rect = Rect(),
        var scaleX: Float,
        var scaleY: Float,
        private var yShift: Int
    ) : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline?) {
            view?.background?.copyBounds(rect)
            rect.scale(scaleX, scaleY)
            rect.offset(0, yShift)

            val cornerRadius =
                resources.getDimensionPixelSize(R.dimen.bottom_panel_radius).toFloat()

            outline?.setRoundRect(rect, cornerRadius)
        }
    }

    init {
        inflate(context, R.layout.layout_floating_panel, this)
        outlineProvider = OutlineProvider(scaleX = 1.02f, scaleY = 1.00f, yShift = (-2).dpToPx(context))
    }

    override fun dispatchApplyWindowInsets(platformInsets: WindowInsets): WindowInsets {
        if (initialMargin[3] != 0) return super.dispatchApplyWindowInsets(platformInsets)
        val insets = WindowInsetsCompat.toWindowInsetsCompat(platformInsets)
        val floatingInsets = insets.getInsets(
            WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
        )
        Log.d("TAG", "marginBottom: ${marginBottom}, InsetsBottom: ${floatingInsets.bottom}")
        if (floatingInsets.bottom != 0) {
            initialMargin = intArrayOf(
                marginLeft,
                marginTop,
                marginRight,
                marginBottom + floatingInsets.bottom
            )
            updateLayoutParams<MarginLayoutParams> {
                bottomMargin = initialMargin[3]
            }
        }
        return super.dispatchApplyWindowInsets(platformInsets)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        doOnLayout {
            initialHeight = height
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else if (event.action == MotionEvent.ACTION_UP) {
            onUp()
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(event: MotionEvent): Boolean {
        valueAnimator?.cancel()
        instanceHeight = height
        instanceBottomMargin = marginBottom
        scrollHeight = height
        scrollBottomMargin = marginBottom
        return true
    }

    override fun onShowPress(event: MotionEvent) {
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    private var lastEventTime = 0L
    private var lastFlingSpeed = 0F

    override fun onScroll(event1: MotionEvent?, event2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val distanceMoved = -(scrollHeight + scrollBottomMargin - height - marginBottom)
        val timeDelta = (event2.eventTime - lastEventTime).toFloat()
        val lastVelocity = distanceMoved.absoluteValue / timeDelta
        if (lastVelocity != 0F) lastFlingSpeed = lastVelocity
        val distanceMovedTotal = -(instanceHeight + instanceBottomMargin - height - marginBottom)
        isScrollingUp = distanceMoved >= 0 && distanceMovedTotal >= 0
        val distanceMotionEvent1ToTop = event1!!.y
        if (distanceMotionEvent1ToTop > height) return true
        val distanceMotionEvent2ToTop = -event2.y
        val compensateHeight = (distanceMotionEvent1ToTop + distanceMotionEvent2ToTop + height).toInt()
        valueAnimator?.cancel()
        setHeight(compensateHeight)
        scrollHeight = height
        scrollBottomMargin = marginBottom
        lastEventTime = event2.eventTime
        return true
    }

    override fun onLongPress(event: MotionEvent) {
    }

    override fun onFling(event1: MotionEvent?, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(
            height,
            if (isScrollingUp) windowHeight else initialHeight
        )
        valueAnimator!!.apply {
            addUpdateListener {
                val value = it.animatedValue as Int
                setHeight(value)
            }
            duration = min(max(((windowHeight - height) / lastFlingSpeed).toLong(), defaultMinDuration), defaultMaxDuration)
            interpolator = defaultInterpolator
            start()
        }
        return true
    }

    private fun onUp() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(
            height,
            if (height > retractThreshold * windowHeight) windowHeight else initialHeight
        )
        valueAnimator!!.apply {
            addUpdateListener {
                val value = it.animatedValue as Int
                setHeight(value)
            }
            duration = defaultUpActionDuration
            interpolator = defaultInterpolator
            start()
        }
    }

    private fun calculateProgressTillTop(compensateHeight: Int) : Float =
        min(1f, compensateHeight / windowHeight.toFloat() - ((1f - (height - initialHeight) / (windowHeight - initialHeight).toFloat()) * initialHeight / windowHeight.toFloat()))

    private fun getMarginWithProgress(progress: Float): IntArray =
        intArrayOf(
            (initialMargin[0] * (1 - progress)).toInt(),
            (initialMargin[1] * (1 - progress)).toInt(),
            (initialMargin[2] * (1 - progress)).toInt(),
            (initialMargin[3] * (1 - progress)).toInt()
        )

    private fun setHeight(height: Int) {
        progress = calculateProgressTillTop(height)
        getMarginWithProgress(progress).let {
            val layoutParams = CoordinatorLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                max(
                    min(height, windowHeight), initialHeight
                )
            )
            layoutParams.setMargins(it[0], it[1], it[2], it[3])
            layoutParams.gravity = Gravity.BOTTOM
            this.layoutParams = layoutParams
            onSlide(progress)
        }
    }

    private fun onSlide(progress: Float) {
        onSlideListener?.onSlide(progress)
        val prevState = state
        state = BigDecimal(progress.toDouble()).setScale(3, RoundingMode.HALF_UP).toDouble().let {
            when (it) {
                1.0 -> {
                    SlideStatus.EXPANDED
                }
                0.0 -> {
                    SlideStatus.COLLAPSED
                }
                else -> {
                    SlideStatus.SLIDING
                }
            }
        }
        if (prevState != state) {
            onSlideListener?.onSlideStatusChanged(state)
        }
    }
}