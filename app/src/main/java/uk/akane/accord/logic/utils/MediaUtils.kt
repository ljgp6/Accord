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
import uk.akane.libphonograph.reader.Reader
import uk.akane.libphonograph.reader.ReaderResult

object MediaUtils {
    /**
     * [getAllSongs] gets all of your songs from your local disk.
     *
     * @param context
     * @return
     */
    private fun getAllSongs(context: Context): ReaderResult<MediaItem> {
        return Reader.readFromMediaStore(context,
            { uri, mediaId, mimeType, title, writer, compilation, composer, artist,
              albumTitle, albumArtist, artworkUri, cdTrackNumber, trackNumber,
              discNumber, genre, recordingDay, recordingMonth, recordingYear,
              releaseYear, artistId, albumId, genreId, author, addDate, duration,
              modifiedDate ->
                return@readFromMediaStore MediaItem
                    .Builder()
                    .setUri(uri)
                    .setMediaId(mediaId.toString())
                    .setMimeType(mimeType)
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setIsBrowsable(false)
                            .setIsPlayable(true)
                            .setTitle(title)
                            .setWriter(writer)
                            .setCompilation(compilation)
                            .setComposer(composer)
                            .setArtist(artist)
                            .setAlbumTitle(albumTitle)
                            .setAlbumArtist(albumArtist)
                            .setArtworkUri(artworkUri)
                            .setTrackNumber(trackNumber)
                            .setDiscNumber(discNumber)
                            .setGenre(genre)
                            .setRecordingDay(recordingDay)
                            .setRecordingMonth(recordingMonth)
                            .setRecordingYear(recordingYear)
                            .setReleaseYear(releaseYear)
                            .setExtras(Bundle().apply {
                                if (artistId != null) {
                                    putLong("ArtistId", artistId)
                                }
                                if (albumId != null) {
                                    putLong("AlbumId", albumId)
                                }
                                if (genreId != null) {
                                    putLong("GenreId", genreId)
                                }
                                putString("Author", author)
                                if (addDate != null) {
                                    putLong("AddDate", addDate)
                                }
                                if (duration != null) {
                                    putLong("Duration", duration)
                                }
                                if (modifiedDate != null) {
                                    putLong("ModifiedDate", modifiedDate)
                                }
                                cdTrackNumber?.toIntOrNull()
                                    ?.let { it1 -> putInt("CdTrackNumber", it1) }
                            })
                            .build(),
                    ).build()
            },
            shouldUseEnhancedCoverReading = null,
            shouldLoadPlaylists = false
        )
    }

    fun updateLibraryWithInCoroutine(viewModel: AccordViewModel, context: Context, then: (() -> Unit)? = null) {
        val pairObject = getAllSongs(context)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.mediaItemList.value = pairObject.songList
            viewModel.albumItemList.value = pairObject.albumList!!
            viewModel.artistItemList.value = pairObject.artistList!!
            viewModel.albumArtistItemList.value = pairObject.albumArtistList!!
            viewModel.genreItemList.value = pairObject.genreList!!
            viewModel.dateItemList.value = pairObject.dateList!!
            viewModel.folderStructure.value = pairObject.folderStructure!!
            viewModel.shallowFolderStructure.value = pairObject.shallowFolder!!
            viewModel.allFolderSet.value = pairObject.folders
            then?.invoke()
        }
    }
}