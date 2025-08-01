package uk.akane.accord.logic.utils

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.akane.accord.ui.viewmodels.AccordViewModel
import uk.akane.libphonograph.reader.ReaderResult
import uk.akane.libphonograph.reader.SimpleReader
import uk.akane.libphonograph.reader.SimpleReaderResult

object MediaUtils {
    /**
     * [getAllSongs] gets all of your songs from your local disk.
     *
     * @param context
     * @return
     */
    private fun getAllSongs(context: Context): SimpleReaderResult {
        return SimpleReader.readFromMediaStore(context)
    }

    fun updateLibraryWithInCoroutine(viewModel: AccordViewModel, context: Context, then: (() -> Unit)? = null) {
        val pairObject = getAllSongs(context)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.mediaItemList.value = pairObject.songList
            viewModel.albumItemList.value = pairObject.albumList
            viewModel.artistItemList.value = pairObject.artistList
            viewModel.albumArtistItemList.value = pairObject.albumArtistList
            viewModel.genreItemList.value = pairObject.genreList
            viewModel.dateItemList.value = pairObject.dateList
            viewModel.folderStructure.value = pairObject.folderStructure
            viewModel.shallowFolderStructure.value = pairObject.shallowFolder
            viewModel.allFolderSet.value = pairObject.folders
            then?.invoke()
        }
    }
}