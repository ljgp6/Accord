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
import uk.akane.accord.ui.fragments.browse.AlbumFragment
import uk.akane.accord.ui.fragments.browse.ArtistFragment
import uk.akane.accord.ui.fragments.browse.DateFragment
import uk.akane.accord.ui.fragments.browse.GenreFragment
import uk.akane.accord.ui.fragments.browse.SongFragment

class BrowseViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> SongFragment()
            1 -> AlbumFragment()
            2 -> ArtistFragment()
            3 -> DateFragment()
            4 -> GenreFragment()
            else -> throw IllegalArgumentException("Didn't find desired fragment!")
        }
}