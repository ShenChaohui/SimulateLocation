package com.sch.simulatelocation.ui.selectLocation

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.coder.zzq.smartshow.snackbar.SmartSnackbar
import com.permissionx.guolindev.PermissionX
import com.sch.simulatelocation.R
import kotlinx.android.synthetic.main.activity_selectlocation.*


/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
class SelectLocationActivity : AppCompatActivity(), AMap.OnMapClickListener {

    private var aMap: AMap? = null
    private lateinit var selectLocationViewModel: SelectLocationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectlocation)
        toolbar.run {
            setSupportActionBar(this)
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }

        PermissionX.init(this).permissions(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .request { allGranted, grantedList, deniedList -> }
        selectLocationViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                SelectLocationViewModel::class.java
            )
        initMap(savedInstanceState)
    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapview.run {
            mapview.onCreate(savedInstanceState)
            if (aMap == null) {
                aMap = mapview.map
            }
            val myLocationStyle = MyLocationStyle()
            myLocationStyle.interval(2000)
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
            myLocationStyle.showMyLocation(true)
            aMap?.myLocationStyle = myLocationStyle
            aMap?.uiSettings?.isZoomControlsEnabled = false
            aMap?.isMyLocationEnabled = true
            aMap?.setOnMapClickListener(this@SelectLocationActivity)
        }

    }

    override fun onMapClick(p0: LatLng?) {
        p0?.let {
            aMap!!.clear()
            val markerOptions = MarkerOptions()
            markerOptions.position(p0)
            aMap!!.addMarker(markerOptions)
            aMap!!.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        p0,
                        12f,
                        0f,
                        0f
                    )
                )
            )
            selectLocationViewModel.getLocationInfo(p0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
        selectLocationViewModel.historyLocationLiveData.observe(this) {
            SmartSnackbar.get(this)
                .showIndefinite(it.formatted_address, "确定", object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        selectLocationViewModel.saveLocation()
                        finish()
                    }
                })
        }
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }

}