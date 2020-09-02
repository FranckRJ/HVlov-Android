package com.franckrj.hvlov

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class VideoLibFolderViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val mockHvlovRepository = mockk<HvlovRepository>()

    val mockSavedStateHandle = mockk<SavedStateHandle>()

    @InjectMockKs
    lateinit var videoLibFolderViewModel: VideoLibFolderViewModel

    @Before
    fun setup() {
        every { mockSavedStateHandle.get<String>("ARG_FOLDER_PATH") } returns ""
        every { mockHvlovRepository.getEntriesForPath(any()) } returns flow { }

        Dispatchers.setMain(TestCoroutineDispatcher())
        MockKAnnotations.init(this)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `construction -- try to get entries from the repository`() {
        verify { mockHvlovRepository.getEntriesForPath("") }
    }
}
