package com.franckrj.hvlov

import org.json.JSONArray
import javax.inject.Inject

/**
 * Service for parsing [HvlovEntry]s out of HVlov server responses.
 */
class HvlovParser @Inject constructor() {
    /**
     * Extract all [HvlovEntry]s from a string.
     *
     * @param pageSource The string where the HVlov entries will be extracted.
     * @return The list of [HvlovEntry]s extracted from the [pageSource].
     */
    fun getListOfHvlovEntries(pageSource: String): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()
        val listJsonOfHvlovEntries = JSONArray(pageSource)

        for (index in 0 until listJsonOfHvlovEntries.length()) {
            val jsonHvlovEntry = listJsonOfHvlovEntries.getJSONObject(index)

            listOfHvlovEntries.add(
                when (jsonHvlovEntry.getString("type")) {
                    "video" -> HvlovEntry.Video(jsonHvlovEntry.getString("title"), jsonHvlovEntry.getString("url"))
                    "folder" -> HvlovEntry.Folder(jsonHvlovEntry.getString("title"), jsonHvlovEntry.getString("url"))
                    else -> throw Exception("Unexpected type of HVlov entry")
                }
            )
        }

        return listOfHvlovEntries
    }
}
