package uk.akane.accord.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import uk.akane.accord.R
import uk.akane.accord.logic.applyOffsetListener
import uk.akane.accord.logic.enableEdgeToEdgePaddingListener

class LibraryFragment: Fragment() {
    private lateinit var appBarLayout: AppBarLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_library, container, false)
        appBarLayout = rootView.findViewById(R.id.appbarlayout)

        appBarLayout.enableEdgeToEdgePaddingListener()
        appBarLayout.applyOffsetListener()

        return rootView
    }
}