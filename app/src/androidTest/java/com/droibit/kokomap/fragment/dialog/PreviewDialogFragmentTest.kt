package com.droibit.kokomap.fragment.dialog

import android.graphics.Bitmap
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.test.ActivityInstrumentationTestCase2
import android.test.suitebuilder.annotation.LargeTest
import com.droibit.kokomap.MainActivity
import com.droibit.kokomap.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

/**
 * [PreviewDialogFragment] クラスの単体テスト
 *
 * @since 2015/07/03
 * @author kumagai
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PreviewDialogFragmentTest: ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {

    private var mActivity: MainActivity by Delegates.notNull()
    private var mSnapshot: Bitmap by Delegates.notNull()

    @Before
    fun testSetUp() {
        super.setUp()
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity()
        mSnapshot = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_4444)
    }

    @After
    fun testTearDown() {
        super.tearDown()

        mSnapshot.recycle()
    }

    @Test
    fun testPositiveButtonIsDone() {
        // 他アプリからの起動の場合は「完了」のテキスト
        mActivity.showPreviewDialog(mSnapshot, launchedPickMode = true)
        onView(withId(android.R.id.button1)).check(matches(withText(mActivity.getString(R.string.text_done))))
        onView(withId(android.R.id.button2)).check(matches(withText(mActivity.getString(R.string.text_retake))))
    }

    @Test
    fun testPositiveButtonIsSave() {
        // 通常の起動の場合は「保存」のテキスト
        mActivity.showPreviewDialog(mSnapshot, launchedPickMode = false)
        onView(withId(android.R.id.button1)).check(matches(withText(mActivity.getString(R.string.text_save))))
        onView(withId(android.R.id.button2)).check(matches(withText(mActivity.getString(R.string.text_retake))))
    }
}