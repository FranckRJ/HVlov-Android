package com.franckrj.hvlov

import org.junit.Assert.assertEquals
import org.junit.Test

class LoadableValueTest {
    @Test
    fun `error -- create error LoadableValue`() {
        val error = LoadableValue.error(5, "error")

        assertEquals(5, error.value)
        assertEquals("error", error.message)
        assertEquals(LoadableValue.Status.ERROR, error.status)
    }

    @Test
    fun `loading -- create loading LoadableValue`() {
        val loading = LoadableValue.loading(10, "loading")

        assertEquals(10, loading.value)
        assertEquals("loading", loading.message)
        assertEquals(LoadableValue.Status.LOADING, loading.status)
    }

    @Test
    fun `loaded -- create loaded LoadableValue`() {
        val loaded = LoadableValue.loaded(15, "loaded")

        assertEquals(15, loaded.value)
        assertEquals("loaded", loaded.message)
        assertEquals(LoadableValue.Status.LOADED, loaded.status)
    }
}
