package org.wit.hillfortapp.activities

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_hillfort.*
import kotlinx.android.synthetic.main.activity_hillfort.view.*
import org.jetbrains.anko.*
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.adapters.NotesAdapter
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note

class HillfortPresenter(val view: HillfortActivity) : AnkoLogger {

    var app: MainApp = view.application as MainApp
    private var hillfort = HillfortModel()
    private var edit = false

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private val NOTE_REQUEST = 3

    private var location = Location()

    init {
        if (view.intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = view.intent.extras?.getParcelable("hillfort_edit")!!
            view.showHillfort(hillfort)
        }
    }

    fun doGetNotes(): ArrayList<Note>? {
        return app.users.findOneUserHillfortNotes(app.activeUser, hillfort)
    }

    fun doClickNote(note: Note) {
        val intent = Intent(view, NotesActivity::class.java)
        intent.putExtra("note_edit", note)

        // pass current hillfort for update/delete functionality
        intent.putExtra("current_hillfort", hillfort)
        view.startActivityForResult(intent, NOTE_REQUEST)
    }

    private fun doShowNotes() {
        val userNotes = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)
        if (userNotes != null) {
            view.recyclerNotes.adapter = NotesAdapter(userNotes, view)
            view.recyclerNotes.adapter?.notifyDataSetChanged()
        }
    }

    fun doAddOrSave(tempHillfort: HillfortModel) {
        hillfort.name = tempHillfort.name
        hillfort.description = tempHillfort.description
        hillfort.visited = tempHillfort.visited
        hillfort.dateVisited = tempHillfort.dateVisited
        hillfort.location = tempHillfort.location
        if (edit) {
            doShowNotes()
            hillfort.notes = app.users.findOneUserHillfortNotes(app.activeUser, hillfort)!!
            app.users.updateHillfort(
                hillfort, app.activeUser
            )
        } else {
            app.users.createHillfort(hillfort, app.activeUser)
        }

        view.finish()
    }

    fun doCancel() {
        view.finish()
        view.startActivity(Intent(view, HillfortListActivity::class.java))
    }

    fun doDelete() {

        if (edit) {
            val builder = AlertDialog.Builder(view)
            builder.setMessage("Are you sure you want to delete this Hillfort?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                app.users.deleteHillfort(hillfort, app.activeUser)
                dialog.dismiss()
                view.startActivity(Intent(view, HillfortListActivity::class.java))
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        view.finish()
    }

    fun doNext() {
        val index = app.activeUser.hillforts.indexOf(hillfort)
        try {
            view.startActivityForResult(
                view.intentFor<HillfortActivity>().putExtra(
                    "hillfort_edit",
                    app.activeUser.hillforts[index + 1]
                ), 0
            )
        } catch (e: IndexOutOfBoundsException) {
            view.toast("Next Hillfort is Empty!")
        }
    }

    fun doPrevious() {
        val index = app.activeUser.hillforts.indexOf(hillfort)
        try {
            view.startActivityForResult(
                view.intentFor<HillfortActivity>().putExtra(
                    "hillfort_edit",
                    app.activeUser.hillforts[index - 1]
                ), 0
            )
        } catch (e: IndexOutOfBoundsException) {
            view.toast("Previous Hillfort is Empty!")
        }
    }

    fun doSelectImage() {
        showImagePicker(view, IMAGE_REQUEST)
    }

    fun doSetLocation() {
        view.startActivityForResult(
            view.intentFor<MapActivity>().putExtra("location", location),
            LOCATION_REQUEST
        )
    }

    fun doNoteDialog() {
        if (!edit) {
            view.toast("Please create a hillfort before adding notes to it!")
        } else {

            val mDialogView = LayoutInflater.from(view).inflate(R.layout.dialog_note, null)
            val builder = AlertDialog.Builder(view)
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
                    view.toast("Please fill out all fields!")
                } else {
                    val newNote = Note()
                    newNote.title = noteTitle.text.toString()
                    newNote.content = noteContent.text.toString()
                    app.users.createNote(app.activeUser, hillfort, newNote)
                    dialog.dismiss()
                    doShowNotes()
                }
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                view.info("RECEIVED REQ")
                val builder = AlertDialog.Builder(view)
                builder.setMessage("This will reset the existing images, continue?")
                builder.setPositiveButton("YES") { dialog, _ ->
                    if (data != null) {
                        val clipImages = ArrayList<String>()
                        // if multiple images selected
                        if (data.clipData != null) {
                            if (data.clipData!!.itemCount > 4) {
                                view.toast("Exceeded maximum of 4 images")
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
                        view.hillfortMoreImagesView.removeAllViews()

                        // reassign array to images from gallery
                        hillfort.images = clipImages

                        // add new image(s) into view
                        view.renderImages(clipImages)
                        view.hillfortMainImage.setImageBitmap(
                            readImageFromPath(
                                view,
                                clipImages[0]
                            )
                        )
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
                    view.hillfortMapView.getMapAsync {
                        view.setMapLocation(it, latLng)
                    }
                }
            }
            NOTE_REQUEST -> {
                if (data != null) {
                    doShowNotes()
                }
            }
        }
    }
}
