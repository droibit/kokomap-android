package com.droibit.kokomap.fragment.dialog

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.test.ActivityInstrumentationTestCase2
import android.test.suitebuilder.annotation.LargeTest
import com.droibit.kokomap.MainActivity
import com.droibit.kokomap.R
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

/**
 * [BalloonDialogFragment] クラスの単体テスト
 *
 * @since 2015/07/03
 * @author kumagai
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class BalloonDialogFragmentTest: ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {

    private var mActivity: MainActivity by Delegates.notNull()

    @Before
    fun testSetUp() {
        super.setUp()
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity()
    }

    @After
    fun testTearDown() {
        super.tearDown()
    }

    @Test
    fun testInputText() {
        mActivity.showBalloonDialog()

        onView(withId(android.R.id.button1)).check(matches(not(isEnabled())))

        // FIXME: 日本語キーボードの場合失敗する
        onView(withId(R.id.balloon_snippet)).perform(typeText("Test"), closeSoftKeyboard())
                                            .check(matches(withText("Test")))
        onView(withId(android.R.id.button1)).check(matches(isEnabled()))
    }

    @Test
    fun testAppearance() {
        mActivity.showBalloonDialog()
        onView(withId(android.R.id.button1)).check(matches(withText(mActivity.getString(android.R.string.ok))))
        onView(withId(android.R.id.button2)).check(matches(withText(mActivity.getString(android.R.string.cancel))))
    }
}