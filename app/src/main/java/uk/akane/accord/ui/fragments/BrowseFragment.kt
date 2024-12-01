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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_browse, container, false)

        return rootView
    }
}