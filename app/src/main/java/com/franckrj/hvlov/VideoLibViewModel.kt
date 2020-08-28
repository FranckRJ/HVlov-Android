package com.franckrj.hvlov

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Type alias representing a list of [HvlovEntry] with a load status.
 */
private typealias LoadableListOfEntries = LoadableValue<List<HvlovEntry>?>

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
     * [MediatorLiveData] of [LoadableListOfEntries] that will be set to the last [LiveData] retrieved from the [HvlovRepository].
     */
    private val _mediatorLiveListOfEntries: MediatorLiveData<LoadableListOfEntries?> = MediatorLiveData()

    /**
     * The last [LiveData] retrieved from the [HvlovRepository].
     */
    private var _lastLiveListOfEntries: LiveData<LoadableListOfEntries?>? = null

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
     * Remove the last [LiveData] retrieved from the [HvlovRepository] from the sources of the [_mediatorLiveListOfEntries].
     */
    private fun resetCurrentLiveListOfEntries() {
        _lastLiveListOfEntries?.let { _mediatorLiveListOfEntries.removeSource(it) }
        _lastLiveListOfEntries = null
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

    // TODO: Detect if an update is already running, and do nothing in this case. Maybe it will be done in the Repository with the cache system.

    fun updateListOfEntries() {
        resetCurrentLiveListOfEntries()

        val newLiveListOfEntries = _hvlovRepository.getEntriesForPath(viewModelScope, currentPath)
        _mediatorLiveListOfEntries.addSource(newLiveListOfEntries) { loadableListOfEntries ->
            _mediatorLiveListOfEntries.value = loadableListOfEntries
        }
        _lastLiveListOfEntries = newLiveListOfEntries
    }

    /**
     * Function used to access the [_mediatorLiveListOfEntries] in an immutable way.
     *
     * @return A [LiveData] referencing [_mediatorLiveListOfEntries].
     */
    fun getListOfEntries(): LiveData<LoadableListOfEntries?> = _mediatorLiveListOfEntries
}
