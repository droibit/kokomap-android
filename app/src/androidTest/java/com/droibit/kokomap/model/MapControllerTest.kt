package com.droibit.kokomap.model

import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import com.droibit.kokomap.model.toMapType
import com.droibit.kokomap.model.FLOW_MARKER_DROP_ONLY
import com.droibit.kokomap.model.FLOW_MARKER_DROP_WITH_BALLON
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
        val satellite = true
        assertEquals(satellite.toMapType(), GoogleMap.MAP_TYPE_SATELLITE)

        assertEquals(false.toMapType(), GoogleMap.MAP_TYPE_NORMAL)
    }

    @Test
    public fun testDropFlowType() {
        val withBalloon = true
        assertEquals(withBalloon.toDropFlow(), FLOW_MARKER_DROP_WITH_BALLON)

        assertEquals(false.toDropFlow(), FLOW_MARKER_DROP_ONLY)
    }
}