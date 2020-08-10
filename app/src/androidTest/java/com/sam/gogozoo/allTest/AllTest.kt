package com.sam.gogozoo.allTest


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.sam.gogozoo.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AllTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )

    @Test
    fun allTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.google_sign_in_button), withText("使用 Google 帳戶繼續"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val constraintLayout = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout.perform(click())

        val constraintLayout2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout2.perform(click())

        val constraintLayout3 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout3.perform(click())

        val constraintLayout4 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout4.perform(click())

        val constraintLayout5 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout5.perform(click())

        val constraintLayout6 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout6.perform(click())

        val constraintLayout7 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout7.perform(click())

        val constraintLayout8 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout8.perform(click())


        val appCompatImageView = onView(
            allOf(
                withId(R.id.buttonSearch),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(R.id.drawerLayout),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageView.perform(click())

        val materialSearchBar = onView(
            allOf(
                withId(R.id.searchBar),
                childAtPosition(
                    allOf(
                        withId(R.id.searchDialog),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        materialSearchBar.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.mt_editText),
                childAtPosition(
                    allOf(
                        withId(R.id.inputContainer),
                        childAtPosition(
                            withId(R.id.root),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatEditText.perform(ViewActions.replaceText("猴"), ViewActions.closeSoftKeyboard())

        val recyclerView = onView(
            allOf(
                withId(R.id.rcySearch),
                childAtPosition(
                    withId(R.id.searchDialog),
                    1
                )
            )
        )
        Thread.sleep(5000)
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val appCompatTextView = onView(
            allOf(
                withId(R.id.buttonNav), withText("開始導航"),
                childAtPosition(
                    allOf(
                        withId(R.id.infoDialog),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatTextView.perform(click())

        val floatingActionButton = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.speedDial),
                        childAtPosition(
                            withId(R.id.constraintLayout),
                            3
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        floatingActionButton.perform(click())

        val cardView = onView(
            allOf(
                withId(R.id.label_container),
                childAtPosition(
                    allOf(
                        withId(R.id.fab_clear),
                        childAtPosition(
                            withId(R.id.speedDial),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        cardView.perform(click())

        val recyclerView3 = onView(
            allOf(
                withId(R.id.rcyHomeTop),
                childAtPosition(
                    withId(R.id.constraintLayout),
                    4
                )
            )
        )
        Thread.sleep(5000)
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(3, click()))

        val textView2 = Espresso.onData(Matchers.anything())
            .inAdapterView(
                allOf(
                    withId(R.id.mListView),
                    childAtPosition(
                        withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    )
                )
            )
            .atPosition(3)
        Thread.sleep(5000)
        textView2.perform(click())

        val recyclerView4 = onView(
            allOf(
                withId(R.id.rcyFacility),
                childAtPosition(
                    withId(R.id.constraintLayout),
                    8
                )
            )
        )
        Thread.sleep(5000)
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(5, click()))

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.buttonancel),
                childAtPosition(
                    allOf(
                        withId(R.id.infoDialog),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageButton4.perform(click())

        val appCompatImageButton5 = onView(
            allOf(
                withId(R.id.imageCloseFac),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            0
                        )
                    ),
                    9
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageButton5.perform(click())

        val floatingActionButton4 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.speedDial),
                        childAtPosition(
                            withId(R.id.constraintLayout),
                            3
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        floatingActionButton4.perform(click())

        val cardView2 = onView(
            allOf(
                withId(R.id.label_container),
                childAtPosition(
                    allOf(
                        withId(R.id.fab_schedule),
                        childAtPosition(
                            withId(R.id.speedDial),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        cardView2.perform(click())

        val appCompatCheckedTextView = Espresso.onData(Matchers.anything())
            .inAdapterView(
                allOf(
                    withClassName(Matchers.`is`("com.android.internal.app.AlertController\$RecycleListView")),
                    childAtPosition(
                        withClassName(Matchers.`is`("android.widget.FrameLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        Thread.sleep(5000)
        appCompatCheckedTextView.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.buttonEdit),
                childAtPosition(
                    allOf(
                        withId(R.id.bottom_dialog),
                        childAtPosition(
                            withClassName(Matchers.`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            1
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageButton.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.buttonDelete),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rcy_schedule),
                        2
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageButton2.perform(click())

        val constraintLayout9 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(android.R.id.content),
                        childAtPosition(
                            withId(R.id.action_bar_root),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        constraintLayout9.perform(click())

        val appCompatTextView2 = onView(
            allOf(
                withId(R.id.buttonRoute), withText("加入路線"),
                childAtPosition(
                    allOf(
                        withId(R.id.infoDialog),
                        childAtPosition(
                            withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatTextView2.perform(click())

        val appCompatCheckedTextView2 = Espresso.onData(Matchers.anything())
            .inAdapterView(
                allOf(
                    withClassName(Matchers.`is`("com.android.internal.app.AlertController\$RecycleListView")),
                    childAtPosition(
                        withClassName(Matchers.`is`("android.widget.FrameLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        Thread.sleep(5000)
        appCompatCheckedTextView2.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.buttonancel),
                childAtPosition(
                    allOf(
                        withId(R.id.infoDialog),
                        childAtPosition(
                            withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatImageButton3.perform(click())

        val dragFloatActionButton = onView(
            allOf(
                withId(R.id.layoutStepCount),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        dragFloatActionButton.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.buttonStop), withText("結束"),
                childAtPosition(
                    childAtPosition(
                        withClassName(Matchers.`is`("androidx.core.widget.NestedScrollView")),
                        0
                    ),
                    10
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        appCompatButton2.perform(click())

        val tabView = onView(
            allOf(
                withContentDescription("紀錄"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tabs_step),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        tabView.perform(click())

        Thread.sleep(5000)
        Espresso.pressBack()

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.buttonMyLocation),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
                        childAtPosition(
                            withClassName(Matchers.`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        floatingActionButton2.perform(click())

        val floatingActionButton3 = onView(
            allOf(
                withId(R.id.buttonMyLocation),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
                        childAtPosition(
                            withClassName(Matchers.`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        floatingActionButton3.perform(click())


        val buttonOnOff = onView(
            allOf(
                withId(R.id.switchMarkers),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        buttonOnOff.perform(click())

        val buttonOnOff2 = onView(
            allOf(
                withId(R.id.switchMarkers),
                isDisplayed()
            )
        )
        Thread.sleep(5000)
        buttonOnOff2.perform(click())


        Thread.sleep(10000)
    }



    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
