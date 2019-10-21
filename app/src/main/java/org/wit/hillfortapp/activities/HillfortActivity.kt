package org.wit.hillfortapp.activities

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
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

        // set blank map
        with(mapView) {
            onCreate(null)
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
            visited.isChecked = hillfort.visited
            dateVisited.setText(hillfort.dateVisited)

            if (hillfort.images.size != 0) {
                hillfortImage.setImageBitmap(readImageFromPath(this, hillfort.images[0]))
                renderImages(hillfort.images)
            } else {
                hillfortImage.setImageResource(drawable.placeholder)
            }

            location = hillfort.location
            val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
            mapView.getMapAsync {
                setMapLocation(it, latLng)
            }

            btnAdd.setBackgroundResource(drawable.ic_check_circle)
        }

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
                    app.users.updateHillfort(
                        hillfort, app.activeUser
                    )
                } else {
                    app.users.createHillfort(hillfort, app.activeUser)
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

            R.id.item_delete -> {
                if (edit) {
                    val builder = AlertDialog.Builder(this@HillfortActivity)
                    builder.setMessage("Are you sure you want to delete this Hillfort?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        app.users.deleteHillfort(hillfort, app.activeUser)
                        finish()
                    }
                    builder.setNegativeButton("No") { dialog, which ->
                        // do nothing
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                val builder = AlertDialog.Builder(this@HillfortActivity)
                builder.setMessage("This will reset the existing images, continue?")
                builder.setPositiveButton("YES") { dialog, which ->
                    if (data != null) {
                        val clipImages = ArrayList<String>()
                        // if multiple images selected
                        if (data.clipData != null) {
                            if (data.clipData!!.itemCount > 4) {
                                toast("Exceeded maximum of 4 images")
                            } else {
                                val mClipData = data.clipData
                                var counter = 0
                                while (counter < mClipData!!.itemCount) {
                                    info("URI--> " + mClipData.getItemAt(counter).uri)
                                    clipImages.add(mClipData.getItemAt(counter).uri.toString())
                                    counter ++
                                }
                            }
                        } else {
                            clipImages.add(data.data.toString())
                        }

                        // clear all images from view
                        moreImages.removeAllViews()

                        // add new image(s) into view
                        renderImages(clipImages)
                        hillfortImage.setImageBitmap(readImageFromPath(this, clipImages[0]))

                    }
                }

                builder.setNegativeButton("No"){dialog,which ->
                    // do nothing
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    location = data.extras?.getParcelable("location")!!
                    val latLng = LatLng(location.lat, location.lng)
                    mapView.getMapAsync {
                        setMapLocation(it, latLng)
                    }
                }
            }
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

    // helper methods

    // Credit: https://tutorial.eyehunts.com/android/android-date-picker-dialog-example-kotlin/
    @TargetApi(Build.VERSION_CODES.N)
    private fun showDateDialog() {
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

    private fun renderImages(images: ArrayList<String>) {
        // reassign array to images from gallery
        hillfort.images = images

        // create new imageview for each image, ignore first image
        for ((index) in (images.withIndex().drop(1))) {
            val newImageView = ImageView(this)
            moreImages.addView(newImageView)
            newImageView.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            newImageView.layoutParams.width = (moreImages.width / images.size)
            newImageView.setPadding(15,0,15,0)
            newImageView.setImageBitmap(readImageFromPath(this, images[index]))

            // listener to switch small imageview it main imageview
            newImageView.setOnClickListener {
                val thisImageDrawable = newImageView.drawable
                val mainImageDrawable = hillfortImage.drawable

                hillfortImage.setImageDrawable(thisImageDrawable)
                newImageView.setImageDrawable(mainImageDrawable)
            }
        }
    }
}
