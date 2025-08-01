package uk.akane.accord.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import uk.akane.accord.logic.utils.BottomSheetUtils
import uk.akane.libphonograph.items.Album
import uk.akane.libphonograph.items.Artist
import uk.akane.libphonograph.items.Date
import uk.akane.libphonograph.items.FileNode
import uk.akane.libphonograph.items.Genre

class AccordViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        const val BOTTOM_SHEET_STATE_TOKEN = "bottomSheetState"
        const val BOTTOM_SHEET_PEEK_HEIGHT_TOKEN = "bottomSheetPeekHeight"
        const val BOTTOM_NAV_STATE_TOKEN = "bottomNavState"
        const val BOTTOM_NAV_TRANSLATION_TOKEN = "bottomNavTrans"
    }
    var bottomSheetState: BottomSheetUtils.ComponentState
        get() = savedStateHandle[BOTTOM_SHEET_STATE_TOKEN] ?: BottomSheetUtils.ComponentState.HIDDEN
        set(value) {
            savedStateHandle[BOTTOM_SHEET_STATE_TOKEN] = value
        }
    var bottomNavState: BottomSheetUtils.ComponentState
        get() = savedStateHandle[BOTTOM_NAV_STATE_TOKEN] ?: BottomSheetUtils.ComponentState.SHOWN
        set(value) {
            savedStateHandle[BOTTOM_NAV_STATE_TOKEN] = value
        }
    var bottomNavTranslation: Float
        get() = savedStateHandle[BOTTOM_NAV_TRANSLATION_TOKEN] ?: 0f
        set(value) {
            savedStateHandle[BOTTOM_NAV_TRANSLATION_TOKEN] = value
        }
    var bottomSheetPeekHeight: Int
        get() = savedStateHandle[BOTTOM_SHEET_PEEK_HEIGHT_TOKEN] ?: 0
        set(value) {
            savedStateHandle[BOTTOM_SHEET_PEEK_HEIGHT_TOKEN] = value
        }
    val mediaItemList: MutableLiveData<List<MediaItem>> = MutableLiveData()
    val albumItemList: MutableLiveData<List<Album>> = MutableLiveData()
    val albumArtistItemList: MutableLiveData<List<Artist>> = MutableLiveData()
    val artistItemList: MutableLiveData<List<Artist>> = MutableLiveData()
    val genreItemList: MutableLiveData<List<Genre>> = MutableLiveData()
    val dateItemList: MutableLiveData<List<Date>> = MutableLiveData()
    val folderStructure: MutableLiveData<FileNode> = MutableLiveData()
    val shallowFolderStructure: MutableLiveData<FileNode> = MutableLiveData()
    val allFolderSet: MutableLiveData<Set<String>> = MutableLiveData()
}