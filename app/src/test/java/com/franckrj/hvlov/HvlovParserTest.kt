package com.franckrj.hvlov

import org.junit.Test

class HvlovParserTest {
    @Test
    fun `getListOfHvlovEntries -- return empty list for empty source`() {
        val hvlovParser = HvlovParser()

        assert(hvlovParser.getListOfHvlovEntries("", "").isEmpty())
    }
}
