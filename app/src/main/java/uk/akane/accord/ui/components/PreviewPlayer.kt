package uk.akane.accord.ui.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import uk.akane.accord.R

class PreviewPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var controlMaterialButton: MaterialButton
    init {
        inflate(context, R.layout.layout_preview_player, this)
        controlMaterialButton = findViewById(R.id.control_btn)
        controlMaterialButton.setOnClickListener {
            Log.d("TAG","hi yes")
        }
    }
}