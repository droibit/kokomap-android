package com.droibit.kokomap

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.ActivityInstrumentationTestCase2
import android.test.suitebuilder.annotation.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.click
import android.view.View
import org.hamcrest.Matcher

/**
 * [MainActivity] クラスの単体テスト
 *
 * @since 2015/07/03
 * @author kumagai
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
public class MainActivityTest: ActivityInstrumentationTestCase2<MainActivity>(javaClass<MainActivity>()) {

    private var mActivity: MainActivity by Delegates.notNull()

    @Before
    public fun testSetUp() {
        super.setUp()
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity()
    }

    @After
    public fun testTearDown() {
        super.tearDown()
    }

    @Test
    public fun testShowBalloonDialog() {
        mActivity.showBalloonDialog()
        // FIXME: 日本語キーボードの場合失敗する
        onView(withId(R.id.balloon_snippet)).perform(typeText("Test"), closeSoftKeyboard())
                                            .check(matches(withText("Test")))
        onView(withId(android.R.id.button1)).check(matches(isEnabled()))
                                            .check(matches(withText(mActivity.getString(android.R.string.ok))))
        onView(withId(android.R.id.button2)).check(matches(withText(mActivity.getString(android.R.string.cancel))))
        //onView(withId(android.R.id.button1)).perform(click())
    }

    public fun testShowPreviewDialogFragment() {
    }
}