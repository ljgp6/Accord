package uk.akane.accord.logic.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.view.Choreographer
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd

object AnimationUtils {

    val easingInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
    val decelerateInterpolator: Interpolator = DecelerateInterpolator(1.7f)

    const val FASTEST_DURATION = 150L
    const val FAST_DURATION = 256L
    const val MID_DURATION = 350L

    val addXfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

    inline fun <reified T> createValAnimator(
        fromValue: T,
        toValue: T,
        duration: Long = FAST_DURATION,
        interpolator: TimeInterpolator = easingInterpolator,
        isArgb: Boolean = false,
        crossinline doOnEnd: (() -> Unit) = {},
        crossinline changedListener: (animatedValue: T) -> Unit,
    ) {
        when (T::class) {
            Int::class -> {
                if (!isArgb)
                    ValueAnimator.ofInt(fromValue as Int, toValue as Int)
                else
                    ValueAnimator.ofArgb(fromValue as Int, toValue as Int)
            }
            Float::class -> {
                ValueAnimator.ofFloat(fromValue as Float, toValue as Float)
            }
            else -> throw IllegalArgumentException("No valid animator type found!")
        }.apply {
            this.duration = duration
            this.interpolator = interpolator
            this.addUpdateListener {
                changedListener(this.animatedValue as T)
            }
            this.doOnEnd {
                doOnEnd()
            }
            start()
        }
    }

    open class LinearAnimator<T>(
        initialValue: T,
        targetValue: T,
        override var startDelay: Long = 0L,
        var duration: Long = 300L,
        var interpolator: TimeInterpolator? = null,
        private val listener: Animator.ValueUpdateListener<T>,
        private val lerp: (from: T, to: T, fraction: Float) -> T
    ) : Animator<T> {
        final override var initialValue: T = initialValue
            private set
        final override var targetValue: T = targetValue
            private set
        final override var currentValue: T = initialValue
            private set
        final override val currentVelocity: Float
            get() = throw UnsupportedOperationException("LinearAnimator does not support velocity")

        final override var isRunning = false
            private set

        private var frameCallback: Choreographer.FrameCallback? = null
        private val choreographer = Choreographer.getInstance()
        private var currentCallbackIncrement = 0

        override fun start() {
            currentCallbackIncrement++
            val currentCallback = currentCallbackIncrement

            val durationScale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ValueAnimator.getDurationScale()
            } else {
                1f
            }

            cancel()
            isRunning = true
            var startTime = 0L

            val initialValue = initialValue
            val targetValue = targetValue
            val interpolator = interpolator
            val delay = (startDelay * 1_000_000L * durationScale).toLong()
            val duration = (duration * 1_000_000L * durationScale).toLong()

            frameCallback = Choreographer.FrameCallback { time ->
                if (currentCallback != currentCallbackIncrement) {
                    return@FrameCallback
                }

                if (startTime == 0L) {
                    startTime = time
                }

                val playTime = time - startTime
                if (playTime < delay) {
                    choreographer.postFrameCallback(frameCallback)
                    return@FrameCallback
                }

                val fraction = (playTime - delay).toFloat() / duration
                if (fraction >= 1f) {
                    end()
                } else {
                    val interpolatedFraction = interpolator?.getInterpolation(fraction) ?: fraction
                    currentValue = lerp(initialValue, targetValue, interpolatedFraction)
                    listener.onValueUpdate(this)
                    choreographer.postFrameCallback(frameCallback)
                }
            }

            choreographer.postFrameCallback(frameCallback)
        }

        override fun animateTo(targetValue: T) {
            initialValue = currentValue
            this.targetValue = targetValue
            start()
        }

        override fun snapTo(targetValue: T) {
            currentCallbackIncrement++
            cancel()
            currentValue = targetValue
            this.targetValue = targetValue
            listener.onValueUpdate(this)
        }

        override fun cancel() {
            isRunning = false
            if (frameCallback != null) {
                choreographer.removeFrameCallback(frameCallback)
            }
        }

        override fun end() {
            isRunning = false
            if (frameCallback != null) {
                choreographer.removeFrameCallback(frameCallback)
            }
            currentValue = targetValue
            listener.onValueUpdate(this)
        }
    }

    interface Animator<T> {
        val initialValue: T

        val targetValue: T

        val currentValue: T

        val currentVelocity: Float

        var startDelay: Long

        val isRunning: Boolean

        fun start()

        fun animateTo(targetValue: T)

        fun snapTo(targetValue: T)

        fun cancel()

        fun end()

        fun interface ValueUpdateListener<T> {
            fun onValueUpdate(animator: Animator<T>)
        }
    }
}