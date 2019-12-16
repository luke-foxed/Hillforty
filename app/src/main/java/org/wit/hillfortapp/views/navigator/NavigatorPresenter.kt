package org.wit.hillfortapp.views.navigator

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.hillfortapp.helpers.checkLocationPermissions
import org.wit.hillfortapp.helpers.isPermissionGranted
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView


class NavigatorPresenter(view: BaseView) : BasePresenter(view) {

    private var locationService: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(view)
    private lateinit var myLocation: LatLng

    init {
        if (checkLocationPermissions(view)) {
            doSetCurrentLocation()
        }
    }

    fun populateMap(map: GoogleMap, myLocation: LatLng, hillfortLocation: LatLng) {
        map.uiSettings.isZoomControlsEnabled = true
        val myLocationOptions = MarkerOptions().title("My Location").position(myLocation)
        val hillfortLocationOptions =
            MarkerOptions().title("Hillfort Location").position(hillfortLocation)
        map.addMarker(myLocationOptions).tag = "My Location"
        map.addMarker(hillfortLocationOptions).tag = "Hillfort Location"

    }

    override fun doRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (isPermissionGranted(requestCode, grantResults)) {
            getCurrentLocation()
        }
    }

    private fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            myLocation = LatLng(it.latitude, it.longitude)
        }
    }

    fun getCurrentLocation(): LatLng {
        return myLocation
    }
}