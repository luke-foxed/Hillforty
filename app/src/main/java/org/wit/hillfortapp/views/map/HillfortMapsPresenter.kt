package org.wit.hillfortapp.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView

class HillfortMapsPresenter(view: BaseView) : BasePresenter(view) {

    fun doPopulateMap(map: GoogleMap, hillforts: List<HillfortModel>) {
        map.uiSettings.isZoomControlsEnabled = true
        hillforts.forEach {
            val loc = LatLng(it.location.lat, it.location.lng)
            val options = MarkerOptions().title(it.name).position(loc)
            map.addMarker(options).tag = it
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))
        }
        view?.showHillfort(hillforts[0])
    }

    fun doMarkerSelected(marker: Marker) {
        val hillfort = marker.tag as HillfortModel
        view?.showHillfort(hillfort)
    }

    fun loadHillforts() {
        view?.showHillforts(app.hillforts.findAllHillforts()!!)
    }
}