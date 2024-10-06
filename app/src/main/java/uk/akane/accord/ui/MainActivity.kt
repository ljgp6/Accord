package uk.akane.accord.ui

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.util.Log
import android.view.RoundedCorner
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.android.material.shape.CornerFamily
import uk.akane.accord.R
import uk.akane.accord.logic.enableEdgeToEdgeProperly
import uk.akane.accord.logic.isEssentialPermissionGranted
import uk.akane.accord.logic.utils.MediaUtils
import uk.akane.accord.logic.utils.UiUtils
import uk.akane.accord.setupwizard.SetupWizardActivity
import uk.akane.accord.ui.components.FloatingPanelLayout
import uk.akane.accord.ui.viewmodels.AccordViewModel

class MainActivity : AppCompatActivity() {

    private val accordViewModel: AccordViewModel by viewModels()

    companion object {
        const val DESIRED_SHRINK_RATIO = 0.85f
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var floatingPanelLayout: FloatingPanelLayout
    private lateinit var shrinkContainerLayout: MaterialCardView
    private lateinit var shadeView: View
    private lateinit var screenCorners: UiUtils.ScreenCorners

    private var bottomInset: Int = 0
    private var bottomDefaultRadius: Int = 0

    private var bottomNavigationPanelColor: Int = 0

    private var isWindowColorSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomDefaultRadius = resources.getDimensionPixelSize(R.dimen.bottom_panel_radius)
        bottomNavigationPanelColor = getColor(R.color.bottomNavigationPanelColor)

        if (isEssentialPermissionGranted()) {
            installSplashScreen()
            if (accordViewModel.mediaItemList.value?.isNotEmpty() != true) {
                MediaUtils.updateLibraryWithInCoroutine(accordViewModel, this)
            }
        } else {
            this.startActivity(
                Intent(this, SetupWizardActivity::class.java)
            )
            finish()
            return
        }

        enableEdgeToEdgeProperly()
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_nav)
        floatingPanelLayout = findViewById(R.id.floating)
        shadeView = findViewById(R.id.shade)
        shrinkContainerLayout = findViewById(R.id.shrink_container)

        floatingPanelLayout.onSlideListener = object : FloatingPanelLayout.OnSlideListener {
            override fun onSlideStatusChanged(status: FloatingPanelLayout.SlideStatus) {
                when (status) {
                    FloatingPanelLayout.SlideStatus.EXPANDED -> {

                    }
                    FloatingPanelLayout.SlideStatus.COLLAPSED -> {
                        shrinkContainerLayout.apply {
                            scaleX = 1f
                            scaleY = 1f
                            // setRenderEffect(null)
                        }
                    }
                    FloatingPanelLayout.SlideStatus.SLIDING -> {

                    }
                }
            }

            override fun onSlide(value: Float) {
                if (!isWindowColorSet) {
                    findViewById<View>(R.id.main).setBackgroundColor(
                        getColor(R.color.windowColor)
                    )
                    isWindowColorSet = true
                }
                shadeView.alpha = 0.5f * value
                shrinkContainerLayout.apply {
                    scaleX = (1f - DESIRED_SHRINK_RATIO) * (1f - value) + DESIRED_SHRINK_RATIO
                    scaleY = (1f - DESIRED_SHRINK_RATIO) * (1f - value) + DESIRED_SHRINK_RATIO
                }
                val cornerProgress = (screenCorners.getAvgRadius() - bottomDefaultRadius) * value + bottomDefaultRadius
                floatingPanelLayout.background = ShapeDrawable().apply {
                    paint.color = bottomNavigationPanelColor
                    shape = RoundRectShape(
                        floatArrayOf(
                            cornerProgress, cornerProgress,
                            cornerProgress, cornerProgress,
                            cornerProgress, cornerProgress,
                            cornerProgress, cornerProgress,
                        ),
                        null,  // No inner rectangle
                        null   // No inner radii
                    )
                }
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { v, windowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())
            val windowInsets = windowInsetsCompat.toWindowInsets()!!
            screenCorners = UiUtils.ScreenCorners(
                (windowInsets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0).toFloat(),
                (windowInsets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius ?: 0).toFloat(),
                (windowInsets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: 0).toFloat(),
                (windowInsets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_RIGHT)?.radius ?: 0).toFloat()
            )
            shrinkContainerLayout.shapeAppearanceModel =
                shrinkContainerLayout.shapeAppearanceModel
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, screenCorners.topLeft)
                    .setTopRightCorner(CornerFamily.ROUNDED, screenCorners.topRight)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, screenCorners.bottomLeft)
                    .setBottomRightCorner(CornerFamily.ROUNDED, screenCorners.bottomRight)
                    .build()
            bottomInset = insets.bottom
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

    }

    fun connectBottomNavigationView(listener : OnItemSelectedListener) {
        bottomNavigationView.setOnItemSelectedListener(listener)
    }
}