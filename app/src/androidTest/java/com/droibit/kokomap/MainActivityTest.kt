package com.droibit.kokomap

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.ActivityInstrumentationTestCase2
import android.test.suitebuilder.annotation.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.properties.Delegates

/**
 * [MainActivity] クラスの単体テスト
 *
 * @since 2015/07/03
 * @author kumagai
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest: ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {

    private var mActivity: MainActivity by Delegates.notNull()

    @Before
    fun testSetUp() {
        super.setUp()
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = activity
    }

    @After
    fun testTearDown() {
        super.tearDown()
    }
}