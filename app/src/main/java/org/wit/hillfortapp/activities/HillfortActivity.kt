package org.wit.hillfortapp.activities

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_hillfort.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.adapters.NoteListener
import org.wit.hillfortapp.adapters.NotesAdapter
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note


class HillfortActivity : MainActivity(), NoteListener, AnkoLogger {

    lateinit var app: MainApp
    private lateinit var presenter: HillfortPresenter
    private var hillfort = HillfortModel()
    private var edit = false
    private var moreImageView: LinearLayout? = null

    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort, content_frame)
        moreImageView = findViewById(R.id.hillfortMoreImagesView)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        app = application as MainApp
        presenter = HillfortPresenter(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerNotes.layoutManager = layoutManager
        recyclerNotes.adapter = presenter.doGetNotes()?.let {
            NotesAdapter(
                it, this
            )
        }

        with(hillfortMapView) {
            onCreate(null)
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
            }
        }

        hillfortDateVisited.setOnClickListener {
            presenter.doDateDialog()
        }

        hillfortAddBtn.setOnClickListener {
            if (listOf(
                    hillfortName.text.toString(),
                    hillfortDescription.text.toString(),
                    hillfortDateVisited.text.toString()
                ).contains("")
            ) {
                toast("Please fill out all fields")
            } else {
                val tempHillfort = HillfortModel()
                tempHillfort.name = hillfortName.text.toString().trim()
                tempHillfort.description = hillfortDescription.text.toString().trim()
                tempHillfort.visited = hillfortVisited.isChecked
                tempHillfort.dateVisited = hillfortDateVisited.text.toString().trim()
                tempHillfort.location = location

                presenter.doAddOrSave(tempHillfort)

                // restart activity so that adapter updates
                startActivity(Intent(this@HillfortActivity, HillfortListActivity::class.java))
            }
        }

        hillfortAddNoteBtn.setOnClickListener {
            presenter.doNoteDialog()
        }

        hillfortChooseImageBtn.setOnClickListener {
            presenter.doSelectImage()
        }

        hillfortLocationBtn.setOnClickListener {
            presenter.doSetLocation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popupCancel -> {
                presenter.doCancel()
            }

            R.id.popupDelete -> {
                if (edit) {
                    val builder = AlertDialog.Builder(this@HillfortActivity)
                    builder.setMessage("Are you sure you want to delete this Hillfort?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        presenter.doDelete()
                        dialog.dismiss()
                        startActivity(Intent(this@HillfortActivity, HillfortListActivity::class.java))
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }

            R.id.popupNext -> {
                presenter.doNext()
            }

            R.id.popupPrevious -> {
                presenter.doPrevious()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        info("VIEW RECIEVED, DATA: " + data)
        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNoteClick(note: Note) {
        presenter.doClickNote(note)
    }

    // hillfortMapView methods
    public override fun onResume() {
        hillfortMapView.onResume()
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        hillfortMapView.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        hillfortMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        hillfortMapView.onLowMemory()
    }

    // helper methods

    fun showHillfort(hillfort: HillfortModel) {
        hillfortName.setText(hillfort.name)
        hillfortDescription.setText(hillfort.description)
        hillfortVisited.isChecked = hillfort.visited
        hillfortDateVisited.setText(hillfort.dateVisited)

        if (hillfort.images.size != 0) {
            renderImages(hillfort.images)
            hillfortMainImage.setImageBitmap(readImageFromPath(this, hillfort.images[0]))
        } else {
            hillfortMainImage.setImageResource(R.drawable.placeholder)
        }

        location = hillfort.location
        val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
        hillfortMapView.getMapAsync {
            setMapLocation(it, latLng)
        }

        hillfortAddBtn.setBackgroundResource(R.drawable.ic_check_circle)
    }

    // source: https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
    fun setMapLocation(map: GoogleMap, location: LatLng) {
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

    fun renderImages(images: ArrayList<String>) {
        // create new image view for each image, ignore first image
        for ((index) in (images.withIndex().drop(1))) {
            val newImageView = ImageView(this)

            moreImageView?.addView(newImageView)
            newImageView.setPadding(15,0,15,0)
            newImageView.setImageBitmap(readImageFromPath(this, images[index]))
            newImageView.layoutParams.height = 300
            newImageView.layoutParams.width = 300
            newImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            newImageView.cropToPadding = true

            // listener to switch small image view it main image view
            newImageView.setOnClickListener {
                val thisImageDrawable = newImageView.drawable
                val mainImageDrawable = hillfortMainImage.drawable

                hillfortMainImage.setImageDrawable(thisImageDrawable)
                newImageView.setImageDrawable(mainImageDrawable)
            }
        }
    }
}