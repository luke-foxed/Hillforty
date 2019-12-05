package org.wit.hillfortapp.views.hillfort

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.helpers.checkLocationPermissions
import org.wit.hillfortapp.helpers.isPermissionGranted
import org.wit.hillfortapp.helpers.showImagePicker
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW
import java.util.*
import kotlin.collections.ArrayList

class HillfortPresenter(view: BaseView) : BasePresenter(view) {

    private var hillfort = HillfortModel()

    private var notes: MutableList<NoteModel>? = mutableListOf()

    private var edit = false

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2

    var locationService: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(view)
    private var location = Location()

    init {
        if (view.intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = view.intent.extras?.getParcelable("hillfort_edit")!!
            doAsync {
                notes = app.users.findOneUserHillfortNotes(
                    app.activeUser.id,
                    hillfort.id
                ) as MutableList<NoteModel>?
                uiThread {
                    view.showHillfort(hillfort)
                    view.showNotes(notes)
                }
            }


        } else {
            if (checkLocationPermissions(view)) {
                doSetCurrentLocation()
            }
        }
    }

    override fun doRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (isPermissionGranted(requestCode, grantResults)) {
            doSetCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            hillfort.location.lat = it.latitude
            hillfort.location.lng = it.longitude
            view?.showHillfort(hillfort)
        }
    }

    fun doClickNote(noteModel: NoteModel) {
        view?.alert("${noteModel.title}\n\n${noteModel.content}")?.show()
    }

    fun doAddOrSave(tempHillfort: HillfortModel) {
        hillfort.name = tempHillfort.name
        hillfort.description = tempHillfort.description
        hillfort.visited = tempHillfort.visited
        hillfort.dateVisited = tempHillfort.dateVisited
        hillfort.userID = app.activeUser.id
        hillfort.id = Random().nextInt()

        doAsync {
            if (edit) {
                hillfort.notes = notes!!
                app.users.updateHillfort(hillfort)
            } else {
                app.users.createHillfort(hillfort)
            }
            uiThread {
                view?.finish()
                view?.navigateTo(VIEW.LIST)
            }
        }
    }

    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        doAsync {
            app.users.deleteHillfort(hillfort)
            uiThread {
                view?.finish()
            }
        }
    }

    fun doNext() {
        val index = app.activeUser.hillforts.indexOf(hillfort)
        try {
            view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", app.activeUser.hillforts[index + 1])
        } catch (e: IndexOutOfBoundsException) {
            view?.toast("Next Hillfort is Empty!")
        }
    }

    fun doPrevious() {
        val index = app.activeUser.hillforts.indexOf(hillfort)
        try {
            view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", app.activeUser.hillforts[index - 1])
        } catch (e: IndexOutOfBoundsException) {
            view?.toast("Previous Hillfort is Empty!")
        }
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun doSetLocation() {
        view?.navigateTo(
            VIEW.LOCATION,
            LOCATION_REQUEST,
            "location",
            hillfort.location
        )
    }

    fun doAddNote(title: String, content: String) {
        if (!edit) {
            view?.toast("Please create a hillfort before adding notes to it!")
        } else {
            val newNote = NoteModel()
            newNote.title = title
            newNote.content = content
            newNote.id = notes?.size!!.plus(1)
            newNote.hillfortID = hillfort.id
            newNote.userID = app.activeUser.id
            doAsync {
                app.users.createNote(newNote)
                notes?.add(newNote)
                uiThread {
                    view?.showNotes(notes)
                }
            }
        }
    }

    fun doDeleteNote(noteModel: NoteModel) {
        doAsync {
            app.users.deleteNote(noteModel)
        }
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                val imageArray = ArrayList<String>()

                // if multiple images selected
                if (data.clipData != null) {
                    if (data.clipData!!.itemCount > 4) {
                        view?.toast("Exceeded maximum of 4 images")
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
                view?.showImages(imageArray)
            }
            LOCATION_REQUEST -> {
                location = data.extras?.getParcelable("location")!!
                hillfort.location = location
                val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
                view?.showUpdatedMap(latLng)
            }
        }
    }
}

