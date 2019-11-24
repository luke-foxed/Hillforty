package org.wit.hillfortapp.views.hillfort

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.android.synthetic.main.activity_hillfort.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note
import org.wit.hillfortapp.views.hillfortlist.HillfortListView

class HillfortView : MainView(),
    NoteListener, AnkoLogger {

    lateinit var app: MainApp
    private lateinit var presenter: HillfortPresenter
    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort, content_frame)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        app = application as MainApp
        presenter = HillfortPresenter(this)

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
                startActivity(Intent(this@HillfortView, HillfortListView::class.java))
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
                presenter.doDelete()
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

    fun showHillfort(hillfort: HillfortModel) {
        hillfortName.setText(hillfort.name)
        hillfortDescription.setText(hillfort.description)
        hillfortVisited.isChecked = hillfort.visited
        hillfortDateVisited.setText(hillfort.dateVisited)

        // pull from model if contents have been updates
        showNotes(app.users.findOneUserHillfortNotes(app.activeUser, hillfort))
        showImages(app.users.findOneUserHillfort(hillfort.id, app.activeUser)?.images)

        val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
        hillfortMapView.getMapAsync {
            setMapLocation(it, latLng)
        }

        hillfortAddBtn.setBackgroundResource(R.drawable.ic_check_circle)
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

    internal fun showNotes(notes: ArrayList<Note>?) {
        val layoutManager = LinearLayoutManager(this)
        val recyclerNotes = findViewById<RecyclerView>(R.id.recyclerNotes)
        recyclerNotes.layoutManager = layoutManager
        if (notes != null) {
            recyclerNotes.adapter =
                HillfortNotesAdapter(notes, this)
            recyclerNotes.adapter?.notifyDataSetChanged()

            val swipeHandler = object : HillfortNoteDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter =
                        HillfortNotesAdapter(notes, this@HillfortView)
                    adapter.removeAt(viewHolder.adapterPosition)

                    (recyclerNotes.adapter as HillfortNotesAdapter).notifyDataSetChanged()
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(recyclerNotes)
        }
    }

    internal fun showImages(images: ArrayList<String>?) {
        val imageViewPager = findViewById<ViewPager>(R.id.viewPager)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dotsIndicator)
        if (images != null) {
            imageViewPager.adapter =
                HillfortImageAdapter(
                    images,
                    this
                )
            dotsIndicator.setViewPager(imageViewPager)
            imageViewPager.adapter?.notifyDataSetChanged()
        }
    }

    internal fun showUpdatedMap(location: LatLng) {
        val latLng = LatLng(location.latitude, location.longitude)
        hillfortMapView.getMapAsync { it.clear() }
        hillfortMapView.getMapAsync {
            setMapLocation(it, latLng)
        }
    }
}