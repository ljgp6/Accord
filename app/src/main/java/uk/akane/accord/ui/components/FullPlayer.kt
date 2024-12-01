package uk.akane.accord.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import uk.akane.accord.R
import uk.akane.accord.logic.getUriToDrawable

class FullPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var blendView: BlendView

    init {
        inflate(context, R.layout.layout_full_player, this)
        blendView = findViewById(R.id.blend_view)
        blendView.setImageUri(context.getUriToDrawable(R.drawable.eg))
        blendView.startRotationAnimation()
    }
}