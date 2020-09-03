package com.franckrj.hvlov

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel shared between [VideoLibFolderFragment]s, it contains data about the whole HVlov server.
 *
 * @property _hvlovPreferencesService The service used to access HVlov preferences.
 */
@ExperimentalCoroutinesApi
class VideoLibViewModel @ViewModelInject constructor(
    private val _hvlovPreferencesService: HvlovPreferencesService,
) : ViewModel() {
    /**
     * A [Channel] that only transmit event for when the settings of the HVlov server have changed.
     */
    private val _hvlovServerSettingsChangedChannel = Channel<Unit>(Channel.CONFLATED)

    /**
     * A receive-only, public view of [_hvlovServerSettingsChangedChannel].
     */
    val hvlovServerSettingsChangedChannel: ReceiveChannel<Unit> = _hvlovServerSettingsChangedChannel

    init {
        viewModelScope.launch {
            _hvlovPreferencesService.hvlovServerSettings.collect {
                _hvlovServerSettingsChangedChannel.send(Unit)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _hvlovServerSettingsChangedChannel.close()
    }
}
