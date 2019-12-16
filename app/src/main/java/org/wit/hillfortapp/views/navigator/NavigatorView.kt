package org.wit.hillfortapp.views.navigator

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_navigator.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.BaseView

class NavigatorView : BaseView() {

    private lateinit var presenter: NavigatorPresenter
    private lateinit var map: GoogleMap

    private var hillfortLocation: LatLng = LatLng(0.0, 0.0)
    private var myLocation: LatLng = LatLng(0.0, 0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_navigator, content_frame)

        presenter = initPresenter(NavigatorPresenter(this)) as NavigatorPresenter

        if (intent.hasExtra("hillfort")) {
            val hillfort: HillfortModel = intent.extras?.getParcelable("hillfort")!!
            hillfortLocation = LatLng(hillfort.location.lat, hillfort.location.lng)
        }

        navigatorMap.onCreate(savedInstanceState)
        navigatorMap.getMapAsync {
            map = it
            myLocation = presenter.getCurrentLocation()
            presenter.populateMap(map, myLocation, hillfortLocation)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigatorMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigatorMap.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        navigatorMap.onPause()
    }

    override fun onResume() {
        super.onResume()
        navigatorMap.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigatorMap.onSaveInstanceState(outState)
    }
}