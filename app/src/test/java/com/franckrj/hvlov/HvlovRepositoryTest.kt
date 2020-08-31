package com.franckrj.hvlov

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HvlovRepositoryTest {
    val hvlovServerSettings = HvlovServerSettings("some_url", "some_password", 1)
    val dummyPath = "some_path"
    val dummyPage = "very big big page"
    val loadingEntriesList = LoadableValue.loading<List<HvlovEntry>?>(null)
    val loadedEntriesList = LoadableValue.loaded<List<HvlovEntry>?>(
        listOf(
            HvlovEntry("blabla", "etc/url", HvlovEntry.Type.FOLDER),
            HvlovEntry("autchoz", "tjr/pa/sa", HvlovEntry.Type.VIDEO),
        )
    )
    val errorEntriesList = LoadableValue.error<List<HvlovEntry>?>(null)

    @MockK
    lateinit var mockHvlovPreferencesService: HvlovPreferencesService

    @MockK
    lateinit var mockHvlovParser: HvlovParser

    @MockK
    lateinit var mockWebService: WebService

    @Suppress("unused")
    val mockIoDispatcher = TestCoroutineDispatcher()

    @InjectMockKs
    lateinit var hvlovRepository: HvlovRepository

    @Before
    fun setupMockk() = MockKAnnotations.init(this)

    @Before
    fun initHvlovPrefMock() {
        every { mockHvlovPreferencesService.hvlovServerSettings } returns MutableStateFlow(hvlovServerSettings)
    }

    @Test
    fun `getEntriesForPath -- return list of entries if all goes well`() {
        every { mockWebService.postPage(any(), any()) } returns dummyPage
        every { mockHvlovParser.getListOfHvlovEntries(any(), any()) } returns loadedEntriesList.value!!

        runBlockingTest {
            Assert.assertEquals(
                listOf(loadingEntriesList, loadedEntriesList),
                hvlovRepository.getEntriesForPath(dummyPath).toList()
            )
        }
    }

    @Test
    fun `getEntriesForPath -- return an error if the request fails`() {
        every { mockWebService.postPage(any(), any()) } returns null

        runBlockingTest {
            Assert.assertEquals(
                listOf(loadingEntriesList, errorEntriesList),
                hvlovRepository.getEntriesForPath(dummyPath).toList()
            )
        }
    }
}
