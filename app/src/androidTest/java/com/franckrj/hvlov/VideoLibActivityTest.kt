package com.franckrj.hvlov

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class VideoLibActivityTest {
    @get:Rule
    var rule: RuleChain? =
        RuleChain.outerRule(HiltAndroidRule(this)).around(ActivityScenarioRule(VideoLibActivity::class.java))

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
