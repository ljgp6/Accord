package uk.akane.accord.ui.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import uk.akane.accord.R
import uk.akane.accord.logic.getUriToDrawable
import uk.akane.cupertino.widget.OverlayDivider

class FullPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var initialMargin = IntArray(4)

    private var blendView: BlendView
    private var overlayDivider: OverlayDivider

    init {
        inflate(context, R.layout.layout_full_player, this)

        blendView = findViewById(R.id.blend_view)
        overlayDivider = findViewById(R.id.divider)

        blendView.setImageUri(context.getUriToDrawable(R.drawable.eg))
        blendView.startRotationAnimation()
        clipToOutline = true
    }

    override fun dispatchApplyWindowInsets(platformInsets: WindowInsets): WindowInsets {
        if (initialMargin[3] != 0) return super.dispatchApplyWindowInsets(platformInsets)
        val insets = WindowInsetsCompat.toWindowInsetsCompat(platformInsets)
        val floatingInsets = insets.getInsets(
            WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
        )
        Log.d(TAG, "marginBottom: ${marginBottom}, InsetsBottom: ${floatingInsets.bottom}, marginTop: ${floatingInsets.top}")
        if (floatingInsets.bottom != 0) {
            initialMargin = intArrayOf(
                marginLeft,
                marginTop + floatingInsets.top,
                marginRight,
                marginBottom + floatingInsets.bottom
            )
            Log.d(TAG, "initTop: ${initialMargin[1]}")
            overlayDivider.updateLayoutParams<MarginLayoutParams> {
                topMargin = initialMargin[1] + overlayDivider.marginTop
            }
        }
        return super.dispatchApplyWindowInsets(platformInsets)
    }

    companion object {
        const val TAG = "FullPlayer"
    }
}