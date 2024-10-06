package uk.akane.accord.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import uk.akane.accord.R
import uk.akane.accord.logic.enableEdgeToEdgePaddingListener
import uk.akane.accord.ui.adapters.BrowseViewPagerAdapter
import kotlin.math.absoluteValue
import kotlin.math.max

class BrowseFragment: Fragment() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var containerView: ViewPager2
    private lateinit var browseViewPagerAdapter: BrowseViewPagerAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_browse, container, false)

        appBarLayout = rootView.findViewById(R.id.appbarlayout)
        containerView = rootView.findViewById(R.id.browse_container)
        tabLayout = rootView.findViewById(R.id.tab_layout)

        browseViewPagerAdapter = BrowseViewPagerAdapter(childFragmentManager, lifecycle)
        containerView.adapter = browseViewPagerAdapter
        containerView.offscreenPageLimit = 9999

        TabLayoutMediator(tabLayout, containerView) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.category_songs)
                }
                1 -> {
                    tab.text = getString(R.string.category_albums)
                }
                2 -> {
                    tab.text = getString(R.string.category_artists)
                }
                3 -> {
                    tab.text = getString(R.string.category_dates)
                }
                4 -> {
                    tab.text = getString(R.string.category_genres)
                }
            }
        }.attach()

        appBarLayout.enableEdgeToEdgePaddingListener()
        appBarLayout.applyOffsetListener()

        return rootView
    }

    private fun AppBarLayout.applyOffsetListener() =
        this.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            private val collapsingToolbarLayout: CollapsingToolbarLayout = children.find { it is CollapsingToolbarLayout } as CollapsingToolbarLayout
            private val materialToolbar: MaterialToolbar = collapsingToolbarLayout.children.find { it is MaterialToolbar } as MaterialToolbar
            private var defaultTitleMarginBottom = 0
            init {
                defaultTitleMarginBottom = materialToolbar.titleMarginBottom
                materialToolbar.isTitleCentered = true
            }

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                val progress = verticalOffset.absoluteValue / (appBarLayout.height - resources.getDimensionPixelSize(R.dimen.tablayout_bottom_margin) - resources.getDimensionPixelSize(R.dimen.tablayout_height) - materialToolbar.height - appBarLayout.paddingTop).toFloat()
                val progressTitle = 1f - max(0f, (progress - 0.5f) / 0.5f)

                appBarLayout.background = AppCompatResources.getDrawable(appBarLayout.context, R.drawable.top_app_bar_divider)?.apply {
                    alpha = (progress * 255).toInt()
                }

                val destinationOffset = (-resources.getDimensionPixelSize(R.dimen.toolbar_margin_bottom_offset) * progressTitle + defaultTitleMarginBottom).toInt()
                // Use a flag or condition to ensure this does not repeatedly cause layout changes
                if (materialToolbar.titleMarginBottom != destinationOffset) {
                    materialToolbar.titleMarginBottom = destinationOffset
                }
            }
        })
}