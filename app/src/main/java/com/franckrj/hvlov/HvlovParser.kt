package com.franckrj.hvlov

class HvlovParser private constructor() {
    companion object {
        val instance: HvlovParser by lazy { HvlovParser() }
    }

    private val _hvlovEntryPattern = Regex("""<a href="([^"]*)">([^<]*)</a>""")

    fun getListOfHvlovEntries(pageSource: String, baseUrl: String): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()
        var hvlovEntryMatcher: MatchResult? = _hvlovEntryPattern.find(pageSource)

        while (hvlovEntryMatcher != null) {
            listOfHvlovEntries.add(
                HvlovEntry(
                    hvlovEntryMatcher.groupValues[2],
                    baseUrl + "/" + hvlovEntryMatcher.groupValues[1],
                    HvlovEntry.Type.VIDEO
                )
            )
            hvlovEntryMatcher = hvlovEntryMatcher.next()
        }

        return listOfHvlovEntries
    }
}
