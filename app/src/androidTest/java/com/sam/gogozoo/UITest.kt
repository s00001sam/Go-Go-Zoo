package com.sam.gogozoo

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UITest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickStepCount() {
        // withId(R.id.my_view) is a ViewMatcher
        // click() is a ViewAction
        // matches(isDisplayed()) is a ViewAssertion
        onData(withId(R.id.layoutStepCount))
            .perform(click())
            .check(matches(isDisplayed()))
    }

}