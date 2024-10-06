package uk.akane.accord.ui.adapters.browse

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import uk.akane.accord.R
import uk.akane.accord.logic.utils.ImageUtils.load
import uk.akane.accord.logic.utils.SortedUtils

class SongAdapter : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private val unsortedList: MutableList<MediaItem> = mutableListOf()
    private val sortedList: MutableList<SortedUtils.Item<MediaItem>> = mutableListOf()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView? = view.findViewById(R.id.cover)
        val title: TextView? = view.findViewById(R.id.title)
        val subtitle: TextView? = view.findViewById(R.id.subtitle)
        val header: TextView? = view.findViewById(R.id.header)
    }

    fun update(updatedList: MutableList<MediaItem>) {

        unsortedList.clear()
        unsortedList.addAll(updatedList)

        val map = mutableMapOf<Char, MutableList<MediaItem>>()

        ('A'..'Z').forEach { map[it] = mutableListOf() }
        map['#'] = mutableListOf()

        for (mediaItem in unsortedList) {
            val title = (mediaItem.mediaMetadata.title?.firstOrNull()?.uppercase() ?: "#")[0]
            if (title in 'A'..'Z') {
                map[title]?.add(mediaItem)
            } else {
                map['#']?.add(mediaItem)
            }
        }

        for ((key, value) in map) {
            if (value.isNotEmpty()) {
                sortedList.add(SortedUtils.Item(key.toString(), null))
                value.sortedBy { it.mediaMetadata.title.toString() }.forEach {
                    sortedList.add(SortedUtils.Item(null, it))
                }
            }
        }

        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else if (sortedList[position - 1].title != null) {
            Log.d("TAG", "Hoooo, ${sortedList[position - 1].title}")
            2
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(
            when (viewType) {
                0 -> R.layout.layout_master_control
                1 -> R.layout.layout_song_item
                2 -> R.layout.layout_item_header
                else -> throw IllegalArgumentException()
            },
            parent,
            false
        )
        )

    override fun getItemCount(): Int =
        sortedList.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val mediaItem = sortedList[position - 1].content!!
                holder.cover!!.post {
                    mediaItem.mediaMetadata.artworkUri?.let { holder.cover.load(it) }
                }
                holder.title!!.text = mediaItem.mediaMetadata.title
                holder.subtitle!!.text = mediaItem.mediaMetadata.artist
                holder.itemView.isClickable = true
            }
            2 -> {
                holder.header!!.text = sortedList[position - 1].title!!
            }
        }
    }
}