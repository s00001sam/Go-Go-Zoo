package com.sam.gogozoo

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Util.getDinstance
import com.sam.gogozoo.util.Util.getEmailName
import com.sam.gogozoo.util.Util.sortByMeter
import com.sam.gogozoo.util.Util.to2fString
import com.sam.gogozoo.util.Util.to3fString
import com.sam.gogozoo.util.Util.toGeos
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UnitTest {

    lateinit var repository: ZooRepository

    @Before
    fun setup(){

    }


    @Test
    fun sortByMeterTest() {
        val infos = listOf(
            NavInfo(title = "國王企鵝", meter = 20),
            NavInfo(title = "黑角企鵝", meter = 150),
            NavInfo(title = "企鵝館", meter = 2),
            NavInfo(title = "駱駝", meter = 1500)
        )
        val sortInfo = listOf(
            NavInfo(title = "企鵝館", meter = 2),
            NavInfo(title = "國王企鵝", meter = 20),
            NavInfo(title = "黑角企鵝", meter = 150),
            NavInfo(title = "駱駝", meter = 1500)
        )
        assertEquals(sortInfo, sortByMeter(infos))
    }

    @Test
    fun getEmailNameTest() {
        val email = "s00001sam@gmail.com"
        val name = "s00001sam"
        assertEquals(name, email.getEmailName())
    }

    @Test
    fun getDistanceTest() {
        val location1 = LatLng(24.5, 121.5)
        val location2 = LatLng(23.2, 122.8)
        val distance =  195892
        assertEquals(distance, location1.getDinstance(location2))
    }

    @Test
    fun get2fString(){
        val double1 = 0.2553
        val answer = "0.26"
        assertEquals(answer, double1.to2fString())
    }

    @Test
    fun get3fString(){
        val double1 = 1.32638
        val answer = "1.326"
        assertEquals(answer, double1.to3fString())
    }

    @Test
    fun toGeosTest(){
        val geoString = "MULTIPOINT ((121.5829850 24.9965557), (121.5837092 24.9955201))"
        val geoString2 = "MULTIPOINT ((121.5829850\n24.9965557)"
        val geos = listOf<GeoPoint>(
            GeoPoint(24.9965557, 121.5829850),
            GeoPoint(24.9955201, 121.5837092)
        )
        val geos2 = listOf(GeoPoint(24.9965557, 121.5829850))
        assertEquals(geos, geoString.toGeos())
        assertEquals(geos2, geoString2.toGeos())
    }

}