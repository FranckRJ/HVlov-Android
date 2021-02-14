package com.franckrj.hvlov

import io.mockk.every
import io.mockk.mockkConstructor
import org.json.JSONArray
import org.junit.Before
import org.junit.Test

class HvlovParserTest {
    @Before
    fun setup() {
        // TODO: Fix this test (it throws, it doesn't return 0)
        mockkConstructor(JSONArray::class)
        every { anyConstructed<JSONArray>().length() } returns 0
    }

    @Test
    fun `getListOfHvlovEntries -- return empty list for empty source`() {
        val hvlovParser = HvlovParser()

        assert(hvlovParser.getListOfHvlovEntries("").isEmpty())
    }
}
