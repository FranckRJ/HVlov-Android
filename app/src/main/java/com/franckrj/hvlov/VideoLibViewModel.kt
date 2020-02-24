package com.franckrj.hvlov

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope

private typealias LoadableListOfEntries = LoadableValue<List<HvlovEntry>?>

// TODO: pouvoir changer de folder
// TODO: savedState pour sauvegarder le folder
// TODO: livedata pour le folder
class VideoLibViewModel(private val app: Application) : AndroidViewModel(app) {
    companion object {
        private const val _clientLibVersion: Int = 1
    }

    private lateinit var _hvlovRepository: HvlovRepository
    private val _mediatorLiveListOfEntries: MediatorLiveData<LoadableListOfEntries?> = MediatorLiveData()
    private var _lastLiveListOfEntries: LiveData<LoadableListOfEntries?>? = null

    var hvlovServerSettings = HvlovServerSettings.default
        set(value) {
            field = value

            val currentContext = app.applicationContext
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
        val currentContext = app.applicationContext
        val sharedPref = currentContext.getSharedPreferences(
            currentContext.getString(R.string.preferenceFileKey),
            Context.MODE_PRIVATE
        )
        val serverAdress = sharedPref.getString(currentContext.getString(R.string.settingsServerAdress), null) ?: ""
        val serverPassword = sharedPref.getString(currentContext.getString(R.string.settingsServerPassword), null) ?: ""
        hvlovServerSettings = HvlovServerSettings(serverAdress, serverPassword, _clientLibVersion)
    }

    private fun resetCurrentLiveListOfEntries() {
        _lastLiveListOfEntries?.let { _mediatorLiveListOfEntries.removeSource(it) }
        _lastLiveListOfEntries = null
    }

    fun updateListOfEntries() {
        resetCurrentLiveListOfEntries()

        val newLiveListOfEntries = _hvlovRepository.getEntriesForFolder("")
        _mediatorLiveListOfEntries.addSource(newLiveListOfEntries) { loadableListOfEntries ->
            _mediatorLiveListOfEntries.value = loadableListOfEntries
        }
        _lastLiveListOfEntries = newLiveListOfEntries
    }

    fun setServerAccessInfo(serverAdress: String, serverPassword: String) {
        hvlovServerSettings = HvlovServerSettings(serverAdress, serverPassword, hvlovServerSettings.version)
    }

    fun getListOfEntries(): LiveData<LoadableListOfEntries?> = _mediatorLiveListOfEntries
}
