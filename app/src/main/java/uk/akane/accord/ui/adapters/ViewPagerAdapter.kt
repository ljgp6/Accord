package uk.akane.accord.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uk.akane.accord.R
import uk.akane.accord.ui.fragments.BrowseFragment
import uk.akane.accord.ui.fragments.HomeFragment
import uk.akane.accord.ui.fragments.LibraryFragment
import uk.akane.accord.ui.fragments.SearchFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    companion object {
        val tabs: ArrayList<Int> = arrayListOf(
            R.id.home,
            R.id.browse,
            R.id.library,
            R.id.search
        )
    }

    override fun getItemCount(): Int = tabs.count()

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HomeFragment()
            1 -> BrowseFragment()
            2 -> LibraryFragment()
            3 -> SearchFragment()
            else -> throw IllegalArgumentException("Didn't find desired fragment!")
        }
}