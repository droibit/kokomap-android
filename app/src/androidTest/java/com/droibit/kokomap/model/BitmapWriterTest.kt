package com.droibit.kokomap.model

import android.graphics.Bitmap
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.InstrumentationTestCase
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates
import junit.framework.Assert.*
import org.junit.Before
import java.io.File

/**
 * [BitmapWriter] クラスの単体テスト
 *
 * @auther kumagai
 * @since 15/06/23
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
public class BitmapWriterTest: InstrumentationTestCase() {

    private var mWriter: BitmapWriter by Delegates.notNull()

    @Before
    public fun testSetUp() {
        super.setUp()

        injectInstrumentation(InstrumentationRegistry.getInstrumentation())

        mWriter = BitmapWriter(getInstrumentation().getTargetContext())
    }

    @Test
    public fun testFilename() {
        val timeMillis1 = 1435067148000L    // 2015/6/23 22:45:48
        val filename1 = mWriter.makeFilename(timeMillis1)

        assertEquals(filename1, "kokomap_150623224548.png")

        var timeMillis2 = 970801599000L   // 2000/10/06 12:06:39
        val filename2 = mWriter.makeFilename(timeMillis2)

        assertEquals(filename2, "kokomap_001006120639.png")
    }

    @Test
    public fun testWriteBitmap() {
        val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_4444)
        val bitmapFilePath = mWriter.write(bitmap)

        assertNotNull(bitmapFilePath)

        val bitmapFile = File(bitmapFilePath)
        assertTrue(bitmapFile.name.startsWith("kokomap_"))
        assertTrue(bitmapFile.name.endsWith(".jpg"))
    }
}