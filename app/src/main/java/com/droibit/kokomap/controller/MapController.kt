package com.droibit.kokomap.controller

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlin.properties.Delegates

/**
 * @auther kumagai
 * *
 * @since 15/06/07
 */
public class MapController constructor(context: Context): OnMapReadyCallback {

    private val mContext = context
    private var mMap: GoogleMap by Delegates.notNull()

    /** {@inheritDoc} */
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        mMap.setMyLocationEnabled(true)
    }
}
