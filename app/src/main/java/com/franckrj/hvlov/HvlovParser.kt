package com.franckrj.hvlov

class HvlovParser private constructor() {
    companion object {
        val instance: HvlovParser by lazy { HvlovParser() }
    }

    private val _hvlovEntryPattern = Regex("""<(video|folder) url="([^"]*)">([^<]*)</[^>]+>""")

    fun getListOfHvlovEntries(pageSource: String, baseUrl: String): List<HvlovEntry> {
        val listOfHvlovEntries = ArrayList<HvlovEntry>()
        var hvlovEntryMatcher: MatchResult? = _hvlovEntryPattern.find(pageSource)

        while (hvlovEntryMatcher != null) {
            try {
                listOfHvlovEntries.add(
                    HvlovEntry(
                        hvlovEntryMatcher.groupValues[3],
                        baseUrl + "/" + hvlovEntryMatcher.groupValues[2],
                        when (hvlovEntryMatcher.groupValues[1]) {
                            "video" -> HvlovEntry.Type.VIDEO
                            "folder" -> HvlovEntry.Type.FOLDER
                            else -> throw Exception("Invalid entry type")
                        }
                    )
                )
            } catch (e: Exception) {
                // Do nothing, ignore the entry if it fails to be added.
            }
            hvlovEntryMatcher = hvlovEntryMatcher.next()
        }

        return listOfHvlovEntries
    }
}
