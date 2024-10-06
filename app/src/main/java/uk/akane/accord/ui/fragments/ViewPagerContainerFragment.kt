package uk.akane.accord.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import uk.akane.accord.R
import uk.akane.accord.logic.setCurrentItemInterpolated
import uk.akane.accord.ui.MainActivity
import uk.akane.accord.ui.adapters.ViewPagerAdapter

class ViewPagerContainerFragment : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_viewpager_container, container, false)

        viewPager2 = rootView.findViewById(R.id.viewpager2)
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        viewPager2.adapter = viewPagerAdapter
        viewPager2.isUserInputEnabled = false
        viewPager2.offscreenPageLimit = 9999

        (requireActivity() as MainActivity).connectBottomNavigationView {
            Log.d("TAG", "upon selection")
            when (it.itemId) {
                R.id.home -> viewPager2.setCurrentItemInterpolated(0)
                R.id.browse -> viewPager2.setCurrentItemInterpolated(1)
                R.id.library -> viewPager2.setCurrentItemInterpolated(2)
                R.id.search -> viewPager2.setCurrentItemInterpolated(3)
                else -> throw IllegalArgumentException("Illegal itemId: ${it.itemId}")
            }
            true
        }

        return rootView
    }
}