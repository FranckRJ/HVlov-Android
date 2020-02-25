package com.franckrj.hvlov

class HvlovParser private constructor() {
    companion object {
        val instance: HvlovParser by lazy { HvlovParser() }
    }

    private val _hvlovFolderPattern = Regex("""<folder url="([^"]*)">([^<]*)</folder>""")
    private val _hvlovVideoPattern = Regex("""<video url="([^"]*)">([^<]*)</video>""")

    private fun getListOfEntriesMatchingPattern(
        pageSource: String,
        baseUrlParam: String,
        pattern: Regex,
        entryBuilder: (MatchResult, String) -> HvlovEntry
    ): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()
        var hvlovEntryMatcher: MatchResult? = pattern.find(pageSource)

        while (hvlovEntryMatcher != null) {
            listOfHvlovEntries.add(entryBuilder(hvlovEntryMatcher, baseUrlParam))
            hvlovEntryMatcher = hvlovEntryMatcher.next()
        }

        return listOfHvlovEntries
    }

    fun getListOfHvlovEntries(pageSource: String, baseUrlParam: String): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()

        listOfHvlovEntries.addAll(
            getListOfEntriesMatchingPattern(pageSource, baseUrlParam, _hvlovFolderPattern) { matcher, _ ->
                HvlovEntry(
                    matcher.groupValues[2],
                    matcher.groupValues[1],
                    HvlovEntry.Type.FOLDER
                )
            }
        )
        listOfHvlovEntries.addAll(
            getListOfEntriesMatchingPattern(pageSource, baseUrlParam, _hvlovVideoPattern) { matcher, baseUrl ->
                HvlovEntry(
                    matcher.groupValues[2],
                    baseUrl + "/" + matcher.groupValues[1],
                    HvlovEntry.Type.VIDEO
                )
            }
        )

        return listOfHvlovEntries
    }
}
