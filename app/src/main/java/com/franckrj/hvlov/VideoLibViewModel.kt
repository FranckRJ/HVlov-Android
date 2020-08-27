package com.franckrj.hvlov

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Type alias representing a list of [HvlovEntry] with a load status.
 */
private typealias LoadableListOfEntries = LoadableValue<List<HvlovEntry>?>

// TODO: Remake the folder system, maybe with fragment and animation (instead of reloading the list), maybe look into navigation lib.
// TODO: Show the current folder somewhere in the UI.
// TODO: Improve DI (for repo, parser, settings, etc).

/**
 * ViewModel for the [VideoLibActivity].
 *
 * @property _context The application context.
 * @property _state A [SavedStateHandle] used to store data across process death.
 */
class VideoLibViewModel @ViewModelInject constructor(
    @ApplicationContext private val _context: Context,
    @Assisted private val _state: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val CLIENT_LIB_VERSION: Int = 1
        private const val SAVE_CURRENT_PATH: String = "SAVE_CURRENT_PATH"
    }

    /**
     * The service used to retrieve [HvlovEntry] from the server.
     */
    private lateinit var _hvlovRepository: HvlovRepository

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

    /**
     * The settings used to access the HVlov server. Address and password are stored in the [SharedPreferences].
     */
    var hvlovServerSettings = HvlovServerSettings.default
        set(value) {
            field = value

            val currentContext = _context
            val sharedPrefEdit = currentContext.getSharedPreferences(
                currentContext.getString(R.string.preferenceFileKey),
                Context.MODE_PRIVATE
            ).edit()

            sharedPrefEdit.putString(currentContext.getString(R.string.settingsServerAddress), value.url)
            sharedPrefEdit.putString(currentContext.getString(R.string.settingsServerPassword), value.password)

            sharedPrefEdit.apply()

            _hvlovRepository = HvlovRepository(viewModelScope, hvlovServerSettings)
            resetCurrentLiveListOfEntries()
            updateListOfEntries()
        }

    init {
        val currentContext = _context
        val sharedPref = currentContext.getSharedPreferences(
            currentContext.getString(R.string.preferenceFileKey),
            Context.MODE_PRIVATE
        )
        val serverAddress = sharedPref.getString(currentContext.getString(R.string.settingsServerAddress), null) ?: ""
        val serverPassword = sharedPref.getString(currentContext.getString(R.string.settingsServerPassword), null) ?: ""
        hvlovServerSettings = HvlovServerSettings(serverAddress, serverPassword, CLIENT_LIB_VERSION)
    }

    /**
     * Remove the last [LiveData] retrieved from the [HvlovRepository] from the sources of the [_mediatorLiveListOfEntries].
     */
    private fun resetCurrentLiveListOfEntries() {
        _lastLiveListOfEntries?.let { _mediatorLiveListOfEntries.removeSource(it) }
        _lastLiveListOfEntries = null
    }

    /**
     * Set the address and the password in the [HvlovServerSettings] used to access the server. Reset [currentPath] as well.
     *
     * @param serverAddress The new address used for accessing the server.
     * @param serverPassword The new password used for accessing the server.
     */
    fun setServerAccessInfo(serverAddress: String, serverPassword: String) {
        hvlovServerSettings = HvlovServerSettings(serverAddress, serverPassword, hvlovServerSettings.version)
        currentPath = ""
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

        val newLiveListOfEntries = _hvlovRepository.getEntriesForPath(currentPath)
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
