package com.tech.riri.androidApp


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EspressoTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashScreenActivity::class.java)

    @Test
    fun espressoTest() {
        val constraintLayout = onView(
            allOf(
                withId(R.id.frame),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scroll),
                        0
                    ),
                    2
                )
            )
        )
        constraintLayout.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val appCompatButton = onView(
            allOf(
                withId(R.id.select_btn), withText("Upload Image"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scroll),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.playandstop),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scroll),
                        0
                    ),
                    6
                )
            )
        )
        appCompatImageView.perform(scrollTo(), click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.playandstop),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scroll),
                        0
                    ),
                    6
                )
            )
        )
        appCompatImageView2.perform(scrollTo(), click())

        val appCompatImageView3 = onView(
            allOf(
                withId(R.id.playandstop),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scroll),
                        0
                    ),
                    6
                )
            )
        )
        appCompatImageView3.perform(scrollTo(), click())
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
