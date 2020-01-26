package com.franckrj.hvlov

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO: Faire la différence entre une liste vide et une erreur côté serveur (actuellmement seules les erreurs côté client sont gérées)
class HvlovRepository(private val scope: CoroutineScope, private val serverSettings: HvlovServerSettings) {
    private val _hvlovParser = HvlovParser.instance
    private val _webService = WebService.instance

    fun getEntriesForFolder(folder: String): LiveData<LoadableValue<List<HvlovEntry>?>?> {
        val liveEntries = MutableLiveData<LoadableValue<List<HvlovEntry>?>?>()
        liveEntries.value = LoadableValue.loading(null)

        scope.launch(Dispatchers.IO) {
            val form = mapOf("path" to folder, "password" to serverSettings.password)
            val pageContent: String? = _webService.postPage(serverSettings.url, form)

            if (pageContent != null) {
                val listOfHvlovEntry = _hvlovParser.getListOfHvlovEntries(pageContent, serverSettings.url)
                liveEntries.postValue(LoadableValue.loaded(listOfHvlovEntry))
            } else {
                liveEntries.postValue(LoadableValue.error(null))
            }
        }

        return liveEntries
    }
}
