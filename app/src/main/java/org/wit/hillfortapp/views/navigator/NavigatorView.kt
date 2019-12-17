package org.wit.hillfortapp.views.navigator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.activity_navigator.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.BaseView


class NavigatorView : BaseView() {

    private lateinit var presenter: NavigatorPresenter
    private lateinit var map: GoogleMap
    private var myLocation: LatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_navigator, content_frame)

        presenter = initPresenter(NavigatorPresenter(this)) as NavigatorPresenter

        navigatorMap.onCreate(savedInstanceState)
        navigatorMap.getMapAsync {
            map = it
            myLocation = presenter.getCurrentLocation()
            presenter.populateMap(map, myLocation, TravelMode.DRIVING)
        }

        drivingRadioButton.isChecked = true

        walkingRadioButton.setOnClickListener {
            walkingRadioButton.isChecked = true
            map.clear()
            presenter.populateMap(map, myLocation, TravelMode.WALKING)
        }

        drivingRadioButton.setOnClickListener {
            drivingRadioButton.isChecked = true
            map.clear()
            presenter.populateMap(map, myLocation, TravelMode.DRIVING)
        }

        openInMapsButton.setOnClickListener {
            presenter.doOpenMaps()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigator, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigatorMenuBack -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
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