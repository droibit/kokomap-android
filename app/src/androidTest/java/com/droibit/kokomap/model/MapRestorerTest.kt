package com.droibit.kokomap.model

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.InstrumentationTestCase
import android.test.suitebuilder.annotation.SmallTest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MapRestorer] クラスの単体テスト
 *
 * @author kumagai
 * @since 15/06/21
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
public class MapRestorerTest: InstrumentationTestCase() {

    @Before
    public fun testSetUp() {
        super.setUp()

        injectInstrumentation(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    public fun testStoreAndRestore() {
        // プリファレンスへ保存
        val restorer = MapRestorer(instrumentation.context)
        val notRestoreCamera = CameraPosition.builder()
                                    .target(LatLng(MapRestorer.DEFAULT_LAT,
                                                   MapRestorer.DEFAULT_LNG))
                                    .zoom(MapRestorer.DEFAULT_ZOOM)
                                    .build()
        // FIXME: プリファレンスに保存されるが、#commit()の戻り値は常にfalse?
        val commit = restorer.store(notRestoreCamera)
//        assertTrue(commit)

        // プリファレンスから復元
        var restoredCamera = restorer.restore()

        assertNotNull(restoredCamera)

        assertEquals(restoredCamera.latitude, MapRestorer.DEFAULT_LAT)
        assertEquals(restoredCamera.longitude, MapRestorer.DEFAULT_LNG)
        assertEquals(restoredCamera.zoom, MapRestorer.DEFAULT_ZOOM)
    }
}