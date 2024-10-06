package uk.akane.accord.ui.fragments.browse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.akane.accord.R
import uk.akane.accord.ui.adapters.browse.SongAdapter
import uk.akane.accord.ui.viewmodels.AccordViewModel

class SongFragment : Fragment(), Observer<List<MediaItem>> {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val accordViewModel: AccordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_browse_song, container, false)

        recyclerView = rootView.findViewById(R.id.rv)
        songAdapter = SongAdapter()
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = songAdapter
        recyclerView.layoutManager = layoutManager

        accordViewModel.mediaItemList.observeForever(this)
        return rootView
    }

    override fun onDestroyView() {
        accordViewModel.mediaItemList.removeObserver(this)
        super.onDestroyView()
    }

    override fun onChanged(value: List<MediaItem>) {
        Log.d("TAG", "CHANGED!!!!!!")
        songAdapter.update(value.toMutableList())
    }

}