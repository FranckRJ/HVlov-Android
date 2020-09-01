package com.franckrj.hvlov

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class VideoLibActivityTest {
    val hvlovServerSettings = HvlovServerSettings("some_url", "some_password", 1)

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(VideoLibActivity::class.java)

    @BindValue
    @JvmField
    val hvlovPreferencesService: HvlovPreferencesService = mockk<HvlovPreferencesService>().also {
        every { it.hvlovServerSettings } returns MutableStateFlow(hvlovServerSettings)
    }

    @Test
    fun displayErrorWhenServerUnreachable() {
        onView(withId(R.id.text_error_videolib)).check(
            matches(
                allOf(
                    withText(R.string.errorServerUnavailable),
                    isDisplayed()
                )
            )
        )
    }
}
