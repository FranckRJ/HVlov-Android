package com.franckrj.hvlov

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

// TODO: Make the difference between an empty list and a server error, to show the appropriate message.
// TODO: Create a RAM-cache for the latest entries retrieved (like, only the 10 latest URL).

/**
 * Repository for accessing [HvlovEntry]s of a server.
 *
 * @property _hvlovPreferencesService The service used to access HVlov preferences.
 * @property _hvlovParser The service used for parsing the server response and retrieving [HvlovEntry]s.
 * @property _webService The service used for making HTTP requests to the server.
 */
class HvlovRepository @Inject constructor(
    private val _hvlovPreferencesService: HvlovPreferencesService,
    private val _hvlovParser: HvlovParser,
    private val _webService: WebService,
    @IoDispatcher private val _ioDispatcher: CoroutineDispatcher,
) {
    /**
     * Return a [Flow] for the list of entries requested. The flow will run on [Dispatchers.IO].
     *
     * @param path The 'path' parameter that will be passed to the request, to access a specific folder.
     * @return A [Flow] of a [LoadableValue] of the [List] of [HvlovEntry]s corresponding to the requested folder.
     */
    fun getEntriesForPath(path: String): Flow<LoadableValue<List<HvlovEntry>?>> = flow {
        emit(LoadableValue.loading(null))

        val hvlovServerSettings: HvlovServerSettings = _hvlovPreferencesService.hvlovServerSettings.value

        val form = mapOf(
            "path" to path,
            "password" to hvlovServerSettings.password,
            "version" to hvlovServerSettings.version.toString()
        )
        val pageContent: String? = _webService.postPage(hvlovServerSettings.url, form)

        if (pageContent != null) {
            val listOfHvlovEntry = _hvlovParser.getListOfHvlovEntries(pageContent, hvlovServerSettings.url)
            emit(LoadableValue.loaded(listOfHvlovEntry))
        } else {
            emit(LoadableValue.error(null))
        }
    }.flowOn(_ioDispatcher)
}
