package com.franckrj.hvlov

import org.junit.Test

class HvlovParserTest {
    val hvlovParser = HvlovParser.instance

    @Test
    fun `getListOfHvlovEntries -- return empty list for empty source`() {
        assert(hvlovParser.getListOfHvlovEntries("", "").isEmpty())
    }
}
