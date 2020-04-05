package com.franckrj.hvlov

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

private typealias LoadableListOfEntries = LoadableValue<List<HvlovEntry>?>

// TODO: pouvoir aller au folder précédent
// TODO: afficher le chemin du folder
class VideoLibViewModel(private val _app: Application, private val _state: SavedStateHandle) : AndroidViewModel(_app) {
    companion object {
        private const val CLIENT_LIB_VERSION: Int = 1
        private const val SAVE_CURRENT_PATH: String = "SAVE_CURRENT_PATH"
    }

    private lateinit var _hvlovRepository: HvlovRepository
    private val _mediatorLiveListOfEntries: MediatorLiveData<LoadableListOfEntries?> = MediatorLiveData()
    private var _lastLiveListOfEntries: LiveData<LoadableListOfEntries?>? = null

    var currentPath: String
        get() = _state.get(SAVE_CURRENT_PATH) ?: ""
        set(newPath) {
            _state.set(SAVE_CURRENT_PATH, newPath)
            updateListOfEntries()
        }

    var hvlovServerSettings = HvlovServerSettings.default
        set(value) {
            field = value

            val currentContext = _app.applicationContext
            val sharedPrefEdit = currentContext.getSharedPreferences(
                currentContext.getString(R.string.preferenceFileKey),
                Context.MODE_PRIVATE
            ).edit()

            sharedPrefEdit.putString(currentContext.getString(R.string.settingsServerAdress), value.url)
            sharedPrefEdit.putString(currentContext.getString(R.string.settingsServerPassword), value.password)

            sharedPrefEdit.apply()

            _hvlovRepository = HvlovRepository(viewModelScope, hvlovServerSettings)
            resetCurrentLiveListOfEntries()
            updateListOfEntries()
        }

    init {
        val currentContext = _app.applicationContext
        val sharedPref = currentContext.getSharedPreferences(
            currentContext.getString(R.string.preferenceFileKey),
            Context.MODE_PRIVATE
        )
        val serverAdress = sharedPref.getString(currentContext.getString(R.string.settingsServerAdress), null) ?: ""
        val serverPassword = sharedPref.getString(currentContext.getString(R.string.settingsServerPassword), null) ?: ""
        hvlovServerSettings = HvlovServerSettings(serverAdress, serverPassword, CLIENT_LIB_VERSION)
    }

    private fun resetCurrentLiveListOfEntries() {
        _lastLiveListOfEntries?.let { _mediatorLiveListOfEntries.removeSource(it) }
        _lastLiveListOfEntries = null
    }

    fun setServerAccessInfo(serverAdress: String, serverPassword: String) {
        hvlovServerSettings = HvlovServerSettings(serverAdress, serverPassword, hvlovServerSettings.version)
        currentPath = ""
    }

    fun goToPreviousFolder(): Boolean {
        var path = currentPath

        while (path.endsWith("/")) {
            path = path.dropLast(1)
        }

        if (path.isEmpty()) {
            return false
        }

        if (path.contains('/')) {
            currentPath = path.substring(0 until path.lastIndexOf('/'))
        } else {
            currentPath = ""
        }
        return true
    }

    fun updateListOfEntries() {
        // TODO: Détecter si un update est déjà en cours, et ne rien faire si c'est le cas.
        resetCurrentLiveListOfEntries()

        val newLiveListOfEntries = _hvlovRepository.getEntriesForPath(currentPath)
        _mediatorLiveListOfEntries.addSource(newLiveListOfEntries) { loadableListOfEntries ->
            _mediatorLiveListOfEntries.value = loadableListOfEntries
        }
        _lastLiveListOfEntries = newLiveListOfEntries
    }

    fun getListOfEntries(): LiveData<LoadableListOfEntries?> = _mediatorLiveListOfEntries
}
