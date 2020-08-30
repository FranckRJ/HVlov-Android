package com.franckrj.hvlov

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// TODO: Remake the folder system, maybe with fragment and animation (instead of reloading the list), maybe look into navigation lib.
// TODO: Show the current folder somewhere in the UI.

/**
 * ViewModel for the [VideoLibActivity].
 *
 * @property _hvlovPreferencesService The service used to access HVlov preferences.
 * @property _hvlovRepository The service used to retrieve [HvlovEntry] from the server.
 * @property _state A [SavedStateHandle] used to store data across process death.
 */
@ExperimentalCoroutinesApi
class VideoLibViewModel @ViewModelInject constructor(
    private val _hvlovPreferencesService: HvlovPreferencesService,
    private val _hvlovRepository: HvlovRepository,
    @Assisted private val _state: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val SAVE_CURRENT_PATH: String = "SAVE_CURRENT_PATH"
    }

    /**
     * The [LiveData] containing the list of entries to show.
     */
    private val _listOfEntries: MutableLiveData<LoadableValue<List<HvlovEntry>?>?> = MutableLiveData(null)

    /**
     * An immutable, public way to accede [_listOfEntries].
     */
    val listOfEntries: LiveData<LoadableValue<List<HvlovEntry>?>?> = _listOfEntries

    /**
     * The current 'path' parameter used to access the right folder in the server. It's stored in the [SavedStateHandle].
     */
    var currentPath: String
        get() = _state.get(SAVE_CURRENT_PATH) ?: ""
        set(newPath) {
            _state.set(SAVE_CURRENT_PATH, newPath)
            updateListOfEntries()
        }

    init {
        viewModelScope.launch {
            _hvlovPreferencesService.hvlovServerSettings.collect {
                currentPath = ""
            }
        }
    }

    /**
     * If the [currentPath] is constituted of one folder or more, remove the last folder from it. Otherwise do nothing.
     *
     * @return True if the path has been updated, false if nothing has be done.
     */
    fun goToPreviousFolder(): Boolean {
        var path = currentPath

        while (path.endsWith("/")) {
            path = path.dropLast(1)
        }

        if (path.isEmpty()) {
            return false
        }

        currentPath = if (path.contains('/')) {
            path.substring(0 until path.lastIndexOf('/'))
        } else {
            ""
        }
        return true
    }

    /**
     * Update the [listOfEntries] with the data of the [HvlovRepository] for the current [currentPath].
     */
    fun updateListOfEntries() {
        // TODO: Store the last job somewhere and cancel it before launching another update.

        _hvlovRepository.getEntriesForPath(currentPath).onEach {
            _listOfEntries.value = it
        }.launchIn(viewModelScope)
    }
}
