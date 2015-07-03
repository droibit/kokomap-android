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
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.action.ViewActions.click

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
    public fun testFabMenuClick() {
        onView(withId(R.id.fab_menu)).perform(click())
        onView(withId(R.id.fab_marker)).perform(click())
    }
}

