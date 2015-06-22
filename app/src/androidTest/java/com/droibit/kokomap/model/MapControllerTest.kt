package com.droibit.kokomap.model

import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import com.droibit.kokomap.model.toSatelliteMapType
import com.google.android.gms.maps.GoogleMap
import org.junit.runner.RunWith
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * [MapController] クラスの単体テスト
 *
 * @auther kumagai
 * @since 15/06/21
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
public class MapControllerTest {

    @Test
    public fun testSatelliteMapType() {
        assertEquals(true.toSatelliteMapType(), GoogleMap.MAP_TYPE_SATELLITE)
        assertEquals(false.toSatelliteMapType(), GoogleMap.MAP_TYPE_NORMAL)
    }
}