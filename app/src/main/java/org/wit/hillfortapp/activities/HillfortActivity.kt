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
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.jetbrains.anko.warn
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.adapters.ImageAdapter
import org.wit.hillfortapp.adapters.NoteListener
import org.wit.hillfortapp.adapters.NotesAdapter
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note


class HillfortActivity : MainActivity(), NoteListener, AnkoLogger {

    lateinit var app: MainApp
    private var hillfort = HillfortModel()
    private var edit = false

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private val NOTE_REQUEST = 3

    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort, content_frame)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        app = application as MainApp

        with(hillfortMapView) {
            onCreate(null)
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
            }
        }

        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras?.getParcelable("hillfort_edit")!!
            hillfortName.setText(hillfort.name)
            hillfortDescription.setText(hillfort.description)
            hillfortVisited.isChecked = hillfort.visited
            hillfortDateVisited.setText(hillfort.dateVisited)

            // pull from model if contents have been updates
            showNotes(app.users.findOneUserHillfortNotes(app.activeUser, hillfort))
            showImages(app.users.findOneUserHillfort(hillfort.id, app.activeUser)?.images)

            location = hillfort.location
            val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
            hillfortMapView.getMapAsync {
                setMapLocation(it, latLng)
            }

            hillfortAddBtn.setBackgroundResource(R.drawable.ic_check_circle)
        }

        hillfortDateVisited.setOnClickListener {
            try {
                showDateDialog()
            } catch (e: Exception) {
                warn(e.message)
            }
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
                hillfort.name = hillfortName.text.toString().trim()
                hillfort.description = hillfortDescription.text.toString().trim()
                hillfort.visited = hillfortVisited.isChecked
                hillfort.dateVisited = hillfortDateVisited.text.toString().trim()
                hillfort.location = location

                if (edit) {
                    hillfort.notes = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)!!
                    app.users.updateHillfort(
                        hillfort, app.activeUser
                    )
                } else {
                    app.users.createHillfort(hillfort, app.activeUser)
                }
                setResult(RESULT_OK)
                finish()

                // restart activity so that adapter updates
                startActivity(Intent(this@HillfortActivity, HillfortListActivity::class.java))
            }
        }

        hillfortAddNoteBtn.setOnClickListener {

            if (!edit) {
                toast("Please create a hillfort before adding notes to it!")
            } else {

                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null)
                val builder = AlertDialog.Builder(this@HillfortActivity)
                builder.setMessage("Enter note details: ")
                builder.setView(mDialogView)

                val dialog: AlertDialog = builder.create()
                dialog.show()

                val addBtn = dialog.findViewById(R.id.noteDialogAddBtn) as Button
                val cancelBtn = dialog.findViewById(R.id.noteDialogCancelBtn) as Button
                val noteTitle = dialog.findViewById(R.id.noteDialogTitle) as? EditText
                val noteContent = dialog.findViewById(R.id.noteDialogContent) as? EditText

                addBtn.setOnClickListener {

                    if (listOf(noteTitle!!.text.toString(), noteContent!!.text.toString())
                            .contains("")
                    ) {
                        toast("Please fill out all fields!")
                    } else {
                        val newNote = Note()
                        newNote.title = noteTitle.text.toString()
                        newNote.content = noteContent.text.toString()
                        app.users.createNote(app.activeUser, hillfort, newNote)

                        // refresh notes
                        showNotes(app.users.findOneUserHillfortNotes(app.activeUser, hillfort)!!)
                        dialog.dismiss()
                    }
                }

                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }

        hillfortChooseImageBtn.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        hillfortLocationBtn.setOnClickListener {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popupCancel -> {
                finish()
                startActivity(Intent(this@HillfortActivity, HillfortListActivity::class.java))
            }

            R.id.popupDelete -> {
                if (edit) {
                    val builder = AlertDialog.Builder(this@HillfortActivity)
                    builder.setMessage("Are you sure you want to delete this Hillfort?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        app.users.deleteHillfort(hillfort, app.activeUser)
                        dialog.dismiss()
                        finish()
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
                val index = app.activeUser.hillforts.indexOf(hillfort)
                try {
                    startActivityForResult(
                        intentFor<HillfortActivity>().putExtra(
                            "hillfort_edit",
                            app.activeUser.hillforts[index + 1]
                        ), 0
                    )
                } catch (e: IndexOutOfBoundsException) {
                    toast("Next Hillfort is Empty!")
                }
            }

            R.id.popupPrevious -> {
                val index = app.activeUser.hillforts.indexOf(hillfort)
                try {
                    startActivityForResult(
                        intentFor<HillfortActivity>().putExtra(
                            "hillfort_edit",
                            app.activeUser.hillforts[index - 1]
                        ), 0
                    )
                } catch (e: IndexOutOfBoundsException) {
                    toast("Previous Hillfort is Empty!")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                val builder = AlertDialog.Builder(this@HillfortActivity)
                builder.setMessage("This will reset the existing images, continue?")
                builder.setPositiveButton("YES") { dialog, _ ->
                    if (data != null) {
                        val imageArray = ArrayList<String>()

                        // if multiple images selected
                        if (data.clipData != null) {
                            if (data.clipData!!.itemCount > 4) {
                                toast("Exceeded maximum of 4 images")
                            } else {
                                val mClipData = data.clipData
                                var counter = 0
                                while (counter < mClipData!!.itemCount) {
                                    imageArray.add(mClipData.getItemAt(counter).uri.toString())
                                    counter++
                                }
                            }
                        } else {
                            imageArray.add(data.data.toString())
                        }
                        hillfort.images = imageArray
                        showImages(imageArray)
                        dialog.dismiss()
                    }
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    location = data.extras?.getParcelable("location")!!
                    val latLng = LatLng(location.lat, location.lng)
                    hillfortMapView.getMapAsync {
                        setMapLocation(it, latLng)
                    }
                }
            }
            NOTE_REQUEST -> {
                if (data != null) {
                    showNotes(hillfort.notes)
                }
            }
        }
    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this, NotesActivity::class.java)
        intent.putExtra("note_edit", note)

        // pass current hillfort for update/delete functionality
        intent.putExtra("current_hillfort", hillfort)
        startActivityForResult(intent, NOTE_REQUEST)

        // refresh notes list if notes where updates/deleted
        hillfort.notes = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)!!
        showNotes(hillfort.notes)
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
                hillfortDateVisited.setText("$dayOfMonth/${monthOfYear + 1}/$year")
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


    private fun showNotes(notes: ArrayList<Note>?) {
        val layoutManager = LinearLayoutManager(this)
        val recyclerNotes = findViewById<RecyclerView>(R.id.recyclerNotes)
        recyclerNotes.layoutManager = layoutManager
        if (notes != null) {
            recyclerNotes.adapter = NotesAdapter(notes, this)
            recyclerNotes.adapter?.notifyDataSetChanged()
        }
    }

    private fun showImages(images: ArrayList<String>?) {
        val imageViewPager = findViewById<ViewPager>(R.id.viewPager)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dotsIndicator)
        if (images != null) {
            imageViewPager.adapter = ImageAdapter(images, this)
            dotsIndicator.setViewPager(imageViewPager)
            imageViewPager.adapter?.notifyDataSetChanged()
        }
    }
}