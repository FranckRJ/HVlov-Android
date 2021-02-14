package com.franckrj.hvlov

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A service used to access the preferences related to HVlov.
 *
 * @property _context The application context.
 */
@Singleton
class HvlovPreferencesService @Inject constructor(
    @ApplicationContext private val _context: Context
) {
    companion object {
        private const val CLIENT_LIB_VERSION: Int = 2
    }

    /**
     * The settings used to access the HVlov server. Address and password are stored in the [SharedPreferences].
     */
    private val _hvlovServerSettings = MutableStateFlow(HvlovServerSettings.default)

    /**
     * A public, read-only way to access [_hvlovServerSettings].
     */
    val hvlovServerSettings: StateFlow<HvlovServerSettings> = _hvlovServerSettings

    init {
        loadPreferences()
    }

    /**
     * Load all the preferences from a [SharedPreferences].
     */
    private fun loadPreferences() {
        val sharedPref = _context.getSharedPreferences(
            _context.getString(R.string.preferenceFileKey),
            Context.MODE_PRIVATE
        )

        val serverAddress = sharedPref.getString(_context.getString(R.string.settingsServerAddress), null) ?: ""
        val serverPassword = sharedPref.getString(_context.getString(R.string.settingsServerPassword), null) ?: ""

        _hvlovServerSettings.value =
            HvlovServerSettings(serverAddress, serverPassword, CLIENT_LIB_VERSION)
    }

    /**
     * Set the address and the password in the [HvlovServerSettings] preference.
     *
     * @param serverAddress The new address used for accessing the server.
     * @param serverPassword The new password used for accessing the server.
     */
    fun setServerAccessInfo(serverAddress: String, serverPassword: String) {
        val newSettings = HvlovServerSettings(serverAddress, serverPassword, hvlovServerSettings.value.version)

        val sharedPrefEdit = _context.getSharedPreferences(
            _context.getString(R.string.preferenceFileKey),
            Context.MODE_PRIVATE
        ).edit()

        sharedPrefEdit.putString(_context.getString(R.string.settingsServerAddress), newSettings.url)
        sharedPrefEdit.putString(_context.getString(R.string.settingsServerPassword), newSettings.password)

        sharedPrefEdit.apply()
        _hvlovServerSettings.value = newSettings
    }
}
