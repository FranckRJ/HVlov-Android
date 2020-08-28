package com.franckrj.hvlov

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Make the difference between an empty list and a server error, to show the appropriate message.
// TODO: Create a RAM-cache for the latest entries retrieved (like, only the 10 latest URL).

/**
 * Repository for accessing [HvlovEntry]s of a server.
 *
 * @property _hvlovPreferencesService The service used to access HVlov preferences.
 */
@ExperimentalCoroutinesApi
class HvlovRepository @Inject constructor(
    private val _hvlovPreferencesService: HvlovPreferencesService,
) {
    /**
     * The service used for parsing the server response and retrieving [HvlovEntry]s.
     */
    private val _hvlovParser = HvlovParser.instance

    /**
     * The service used for making HTTP requests to the server.
     */
    private val _webService = WebService.instance

    /**
     * Return a [LiveData] for the list of entries requested.
     *
     * @param scope The coroutine scope in which the request will be executed.
     * @param path The 'path' parameter that will be passed to the request, to access a specific folder.
     * @return A [LiveData] of a [LoadableValue] of the [List] of [HvlovEntry]s corresponding to the requested folder.
     */
    fun getEntriesForPath(scope: CoroutineScope, path: String): LiveData<LoadableValue<List<HvlovEntry>?>?> {
        val liveEntries = MutableLiveData<LoadableValue<List<HvlovEntry>?>?>()
        liveEntries.value = LoadableValue.loading(null)

        val hvlovServerSettings: HvlovServerSettings = _hvlovPreferencesService.hvlovServerSettings.value

        // TODO: Move this part to its own function.
        scope.launch(Dispatchers.IO) {
            val form = mapOf(
                "path" to path,
                "password" to hvlovServerSettings.password,
                "version" to hvlovServerSettings.version.toString()
            )
            val pageContent: String? = _webService.postPage(hvlovServerSettings.url, form)

            if (pageContent != null) {
                val listOfHvlovEntry = _hvlovParser.getListOfHvlovEntries(pageContent, hvlovServerSettings.url)
                liveEntries.postValue(LoadableValue.loaded(listOfHvlovEntry))
            } else {
                liveEntries.postValue(LoadableValue.error(null))
            }
        }

        return liveEntries
    }
}
