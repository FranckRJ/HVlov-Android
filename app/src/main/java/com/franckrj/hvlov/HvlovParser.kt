package com.franckrj.hvlov

import javax.inject.Inject

/**
 * Service for parsing [HvlovEntry]s out of HVlov server responses.
 */
class HvlovParser @Inject constructor() {
    private val _hvlovFolderPattern = Regex("""<folder url="([^"]*)">([^<]*)</folder>""")
    private val _hvlovVideoPattern = Regex("""<video url="([^"]*)">([^<]*)</video>""")

    /**
     * Return the list of [HvlovEntry]s matching a pattern.
     *
     * @param pageSource The string where entries are extracted.
     * @param pattern The pattern used to match the entries.
     * @param entryBuilder A function used to build an [HvlovEntry] from a [MatchResult].
     * @return The list of built [HvlovEntry]s.
     */
    private fun getListOfEntriesMatchingPattern(
        pageSource: String,
        pattern: Regex,
        entryBuilder: (MatchResult) -> HvlovEntry
    ): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()
        var hvlovEntryMatcher: MatchResult? = pattern.find(pageSource)

        while (hvlovEntryMatcher != null) {
            listOfHvlovEntries.add(entryBuilder(hvlovEntryMatcher))
            hvlovEntryMatcher = hvlovEntryMatcher.next()
        }

        return listOfHvlovEntries
    }

    // TODO: Try to remove the param 'baseUrl', because currently HVlov entries aren't build by only extracting data
    //       from the pageSource, some data are inserted as well (baseUrl). It's not the purpose of this service.

    /**
     * Extract all [HvlovEntry]s from a string.
     *
     * @param pageSource The string where the HVlov entries will be extracted.
     * @param baseUrl The URL of the server that will serve as base for video entries url's.
     * @return The list of [HvlovEntry]s extracted from the [pageSource].
     */
    fun getListOfHvlovEntries(pageSource: String, baseUrl: String): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()

        listOfHvlovEntries.addAll(
            getListOfEntriesMatchingPattern(pageSource, _hvlovFolderPattern) { matcher ->
                HvlovEntry.Folder(
                    matcher.groupValues[2],
                    matcher.groupValues[1],
                )
            }
        )
        listOfHvlovEntries.addAll(
            getListOfEntriesMatchingPattern(pageSource, _hvlovVideoPattern) { matcher ->
                HvlovEntry.Video(
                    matcher.groupValues[2],
                    baseUrl + "/" + matcher.groupValues[1],
                )
            }
        )

        return listOfHvlovEntries
    }
}
