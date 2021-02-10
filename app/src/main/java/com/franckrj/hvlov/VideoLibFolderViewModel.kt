package com.franckrj.hvlov

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

// TODO: Remake the folder system, maybe with fragment and animation (instead of reloading the list), maybe look into navigation lib.
// TODO: Show the current folder somewhere in the UI.

/**
 * ViewModel for the [VideoLibFolderFragment]. Contain data about a single folder of [HvlovEntry]s.
 *
 * @property _hvlovRepository The service used to retrieve [HvlovEntry] from the server.
 * @property _state A [SavedStateHandle] used to store data across process death.
 */
@HiltViewModel
class VideoLibFolderViewModel @Inject constructor(
    private val _hvlovRepository: HvlovRepository,
    private val _state: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val ARG_FOLDER_PATH: String = "ARG_FOLDER_PATH"
    }

    /**
     * The [LiveData] containing the list of entries to show.
     */
    private val _listOfEntries: MutableLiveData<LoadableValue<List<HvlovEntry>?>?> =
        MutableLiveData(LoadableValue.loading(null))

    /**
     * An immutable, public way to accede [_listOfEntries].
     */
    val listOfEntries: LiveData<LoadableValue<List<HvlovEntry>?>?> = _listOfEntries

    /**
     * The current 'path' parameter used to access the right folder in the server.
     */
    var folderPath: String = _state.get(ARG_FOLDER_PATH) ?: ""

    init {
        updateListOfEntries()
    }

    /**
     * Update the [listOfEntries] with the data of the [HvlovRepository] for the current [folderPath].
     */
    fun updateListOfEntries() {
        // TODO: Store the last job somewhere and cancel it before launching another update.

        _hvlovRepository.getEntriesForPath(folderPath).onEach {
            _listOfEntries.value = it
        }.launchIn(viewModelScope)
    }
}
