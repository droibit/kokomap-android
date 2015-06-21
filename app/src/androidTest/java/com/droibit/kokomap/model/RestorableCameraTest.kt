package com.droibit.kokomap.model

import android.support.test.runner.AndroidJUnit4
import android.test.AndroidTestCase
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Test
import org.junit.runner.RunWith
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull

/**
 * [RestorableCamera] クラスの単体テスト
 *
 * @auther kumagai
 * @since 15/06/21
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
public class RestorableCameraTest {

    @Test
    public fun testCreateRestorableCamera() {
        val camera = RestorableCamera(MapRestorer.DEFAULT_LAT,
                                      MapRestorer.DEFAULT_LNG,
                                      MapRestorer.DEFAULT_ZOOM)

        assertNotNull(camera)

        assertEquals(camera.latLng.latitude, MapRestorer.DEFAULT_LAT)
        assertEquals(camera.latLng.latitude, camera.latitude)

        assertEquals(camera.latLng.longitude, MapRestorer.DEFAULT_LNG)
        assertEquals(camera.latLng.longitude, camera.longitude)

        assertEquals(camera.zoom, MapRestorer.DEFAULT_ZOOM)
    }
}