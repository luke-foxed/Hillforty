package org.wit.hillfortapp.activities

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
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
import org.jetbrains.anko.*
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.R.drawable
import org.wit.hillfortapp.R.layout
import org.wit.hillfortapp.helpers.readImage
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.placemark.activities.MapActivity


class HillfortActivity : AppCompatActivity(), AnkoLogger {

    private var hillfort = HillfortModel()
    private var edit = false

    lateinit var app: MainApp

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_hillfort)
        info("Hillfort Activity started..")

        with(mapView) {
            onCreate(null)
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
            }
        }

        app = application as MainApp

        if (intent.hasExtra("hillfort_edit")) {

            edit = true
            hillfort = intent.extras?.getParcelable("hillfort_edit")!!
            name.setText(hillfort.name)
            description.setText(hillfort.description)
            hillfortImage.setImageBitmap(readImageFromPath(this, hillfort.image))
            visited.isChecked = hillfort.visited
            dateVisited.setText(hillfort.dateVisited)
            location = hillfort.location
            val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
            mapView.getMapAsync {
                setMapLocation(it, latLng)
            }

            btnAdd.setBackgroundResource(drawable.ic_check_circle)
        }

        // if targetAPI is not met
//        dateVisited.setOnClickListener {
//
//        }

        dateVisited.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus) {
                try {
                    showDateDialog()
                } catch (e: Exception) {
                    warn(e.message)
                }
            }
        }

        btnAdd.setOnClickListener {

            println("ACTIVE USER --> ${app.activeUser}")
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

                // TODO: Find way to access Hillfort MemStore functions
                if (edit) {
                    app.activeUser.hillforts[hillfort.id] = hillfort
                    // app.hillforts.update(hillfort.copy())
                } else {
                    app.activeUser.hillforts.add(hillfort.copy())
                    // app.hillforts.create(hillfort.copy())
                }

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
                    chooseImage.text = "Add Image"
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    location = data.extras?.getParcelable<Location>("location")!!
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun showDateDialog() {
        // Credit: https://tutorial.eyehunts.com/android/android-date-picker-dialog-example-kotlin/
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateVisited.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            },
            year, month, day
        )
        dpd.show()
    }

    // source: https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
    private fun setMapLocation(map: GoogleMap, location: LatLng) {
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

    // mapView methods
    public override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
