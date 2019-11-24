package org.wit.hillfortapp.views.map

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.content_hillfort_maps.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.models.HillfortModel


class HillfortMapsView : MainView(), GoogleMap.OnMarkerClickListener {

    lateinit var app: MainApp
    private lateinit var presenter: PlacemarkMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort_maps, content_frame)

        app = application as MainApp
        presenter = PlacemarkMapPresenter(this)

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync {
            presenter.doPopulateMap(it)
        }
    }

    fun showHillfort(hillfort: HillfortModel) {
        currentTitle.text = hillfort.name
        currentDescription.text = hillfort.description
        currentImage.setImageBitmap(readImageFromPath(this, hillfort.images[0]))
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Int
        val hillfort = app.users.findOneHillfort(tag)
        currentTitle.text = hillfort!!.name
        currentDescription.text = hillfort.description
        currentImage.setImageBitmap(readImageFromPath(this, hillfort.images[0]))
        return true
    }
}