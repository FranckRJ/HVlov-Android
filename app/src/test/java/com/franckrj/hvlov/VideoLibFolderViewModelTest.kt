package com.franckrj.hvlov

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class VideoLibFolderViewModelTest {
    companion object {
        private const val SAVE_CURRENT_PATH: String = "SAVE_CURRENT_PATH"
    }

    val hvlovServerSettings = HvlovServerSettings("some_url", "some_password", 1)
    var currentPath = ""
    val currentPathSlot = slot<String>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val mockHvlovPreferencesService = mockk<HvlovPreferencesService>()

    val mockHvlovRepository = mockk<HvlovRepository>()

    val mockSavedStateHandle = mockk<SavedStateHandle>()

    @InjectMockKs
    lateinit var videoLibFolderViewModel: VideoLibFolderViewModel

    @Before
    fun setup() {
        every { mockHvlovPreferencesService.hvlovServerSettings } returns MutableStateFlow(hvlovServerSettings)
        every { mockSavedStateHandle.get<String>(SAVE_CURRENT_PATH) } returns currentPath
        every { mockSavedStateHandle.set(SAVE_CURRENT_PATH, capture(currentPathSlot)) } answers {
            currentPath = currentPathSlot.captured
        }
        every { mockHvlovRepository.getEntriesForPath(any()) } returns flow { }

        Dispatchers.setMain(TestCoroutineDispatcher())
        MockKAnnotations.init(this)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `goToPreviousFolder -- do nothing when there isn't a previous folder`() {
        currentPath = ""
        assertEquals(false, videoLibFolderViewModel.goToPreviousFolder())
        assertEquals("", currentPath)
    }
}
