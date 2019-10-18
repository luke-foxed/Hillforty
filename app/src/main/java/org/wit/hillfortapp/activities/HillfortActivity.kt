package org.wit.hillfortapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_hillfort.*
import kotlinx.android.synthetic.main.card_placement.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.helpers.readImage
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.placemark.activities.MapActivity


class HillfortActivity : AppCompatActivity(), AnkoLogger {

    var hillfort = HillfortModel()
    var edit = false

    lateinit var app: MainApp

    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        info("Hillfort Activity started..")

        with(mapView) {
            onCreate(null)
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
                setMapLocation(it)
            }
        }

        app = application as MainApp


        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras?.getParcelable<HillfortModel>("hillfort_edit")!!
            name.setText(hillfort.name)
            description.setText(hillfort.description)
            hillfortImage.setImageBitmap(readImageFromPath(this, hillfort.image))
            visited.isChecked = hillfort.visited
            dateVisited.setText(hillfort.dateVisited)
            location = hillfort.location
            mapView.getMapAsync {
                setMapLocation(it)
            }

            if (hillfort.image != null) {
                chooseImage.text = "Change Image"
            }
        }

        btnAdd.setOnClickListener {

            if (listOf(
                    name.text.toString(),
                    description.text.toString(),
                    dateVisited.text.toString()
                ).contains("")
            ) {
                toast("Please fill out all fields")
            } else {

                hillfort.name = name.text.toString()
                hillfort.description = description.text.toString()
                hillfort.visited = visited.isChecked
                hillfort.dateVisited = dateVisited.text.toString()
                hillfort.location = location

                if (edit) {
                    app.hillforts.update(hillfort.copy())
                } else {
                    app.hillforts.create(hillfort.copy())
                }

                info("add Button Pressed: $hillfortName")
                setResult(RESULT_OK)
                finish()
            }
        }

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        hillfortLocation.setOnClickListener {
            startActivityForResult(
                intentFor<MapActivity>().putExtra("location", location),
                LOCATION_REQUEST
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    hillfort.image = data.data.toString()
                    hillfortImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseImage.text = "Edit Image"
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    location = data.extras?.getParcelable<Location>("location")!!
                }
            }
        }
    }

    // source: https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
    private fun setMapLocation(map: GoogleMap) {

        if (edit) {

            val lat: Double = hillfort.location.lat
            val lng: Double = hillfort.location.lng

            val location = LatLng(lat, lng)

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5f))

            with(map) {
                addMarker(
                    MarkerOptions().position(
                        location
                    )
                )
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
    }
}
