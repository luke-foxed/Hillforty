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
import org.jetbrains.anko.*
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.adapters.NoteListener
import org.wit.hillfortapp.adapters.NotesAdapter
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note


class HillfortActivity : MainActivity(), NoteListener, AnkoLogger {

    lateinit var app: MainApp
    private var hillfort = HillfortModel()
    private var edit = false
    private var moreImageView: LinearLayout? = null

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private val NOTE_REQUEST = 3

    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort, content_frame)

        moreImageView = findViewById(R.id.hillfortMoreImagesView)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        recyclerNotes.layoutManager = layoutManager
        recyclerNotes.adapter = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)?.let {
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

        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras?.getParcelable("hillfort_edit")!!
            hillfortName.setText(hillfort.name)
            hillfortDescription.setText(hillfort.description)
            hillfortVisited.isChecked = hillfort.visited
            hillfortDateVisited.setText(hillfort.dateVisited)

            loadNotes()

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

        hillfortDateVisited.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                try {
                    showDateDialog()
                } catch (e: Exception) {
                    warn(e.message)
                }
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
                        toast("No changes made!")
                    } else {
                        val newNote = Note()
                        newNote.title = noteTitle.text.toString()
                        newNote.content = noteContent.text.toString()

                        app.users.createNote(app.activeUser, hillfort, newNote)

                    }
                    dialog.dismiss()
                    loadNotes()
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
            R.id.item_cancel -> {
                finish()
            }

            R.id.item_delete -> {
                if (edit) {
                    val builder = AlertDialog.Builder(this@HillfortActivity)
                    builder.setMessage("Are you sure you want to delete this Hillfort?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        app.users.deleteHillfort(hillfort, app.activeUser)
                        dialog.dismiss()
                        finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadNotes()
        when (requestCode) {
            IMAGE_REQUEST -> {
                val builder = AlertDialog.Builder(this@HillfortActivity)
                builder.setMessage("This will reset the existing images, continue?")
                builder.setPositiveButton("YES") { dialog, _ ->
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
                                    clipImages.add(mClipData.getItemAt(counter).uri.toString())
                                    counter++
                                }
                            }
                        } else {
                            clipImages.add(data.data.toString())

                        }
                        dialog.dismiss()

                        // clear all images from view
                        hillfortMoreImagesView.removeAllViews()

                        // add new image(s) into view
                        renderImages(clipImages)
                        hillfortMainImage.setImageBitmap(readImageFromPath(this, clipImages[0]))
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
                    loadNotes()
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

    private fun renderImages(images: ArrayList<String>) {

        // reassign array to images from gallery
        hillfort.images = images

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

    private fun loadNotes() {
        val userNotes = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)
        if (userNotes != null) {
            showNotes(userNotes)
        }
    }

    private fun showNotes(notes: ArrayList<Note>) {
        recyclerNotes.adapter = NotesAdapter(notes, this)
        recyclerNotes.adapter?.notifyDataSetChanged()
    }
}