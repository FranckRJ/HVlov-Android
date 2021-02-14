package com.franckrj.hvlov

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel shared between [VideoLibFolderFragment]s, it contains data about the whole HVlov server.
 *
 * @property _hvlovPreferencesService The service used to access HVlov preferences.
 */
@HiltViewModel
class VideoLibViewModel @Inject constructor(
    private val _hvlovPreferencesService: HvlovPreferencesService,
) : ViewModel() {
    /**
     * A [MutableSharedFlow] that only transmit event for when the settings of the HVlov server have changed.
     */
    private val _hvlovServerSettingsChanged = MutableSharedFlow<Unit>(0, 1, BufferOverflow.DROP_OLDEST)

    /**
     * A collect-only, public view of [_hvlovServerSettingsChanged].
     */
    val hvlovServerSettingsChanged: SharedFlow<Unit> = _hvlovServerSettingsChanged

    /**
     * The url of the HVlov server.
     */
    val hvlovServerUrl: StateFlow<String> =
        _hvlovPreferencesService.hvlovServerSettings.map { it.url }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        viewModelScope.launch {
            _hvlovPreferencesService.hvlovServerSettings.collect {
                _hvlovServerSettingsChanged.emit(Unit)
            }
        }
    }
}
