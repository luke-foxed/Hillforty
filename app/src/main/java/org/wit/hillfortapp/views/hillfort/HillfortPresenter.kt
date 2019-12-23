package org.wit.hillfortapp.views.hillfort

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.content_hillfort_fab.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.helpers.*
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.ImageModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW
import java.io.File
import java.util.*

class HillfortPresenter(view: BaseView) : BasePresenter(view) {

    private var hillfort = HillfortModel()

    private var notes: ArrayList<NoteModel>? = arrayListOf()
    private var images: ArrayList<ImageModel> = arrayListOf()

    private var edit = false

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private val IMAGE_CAPTURE_REQUEST = 3

    var locationService: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(view)
    private var location = Location()

    init {
        if (view.intent.hasExtra("hillfort_edit")) {
            hillfort = view.intent.extras?.getParcelable("hillfort_edit")!!
            notes = hillfort.notes
            images = hillfort.images

            view.showHillfort(hillfort)

            edit = true

            if(hillfort.isFavourite) {
                view.fabMoreFavourite!!.setColorFilter(Color.rgb(255, 116, 216))
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

    fun doFavourite() {
        hillfort.isFavourite = !hillfort.isFavourite
        app.hillforts.updateHillfort(hillfort)
        app.hillforts.toggleFavourite(hillfort)
        if (hillfort.isFavourite) {
            view?.fabMoreFavourite!!.setColorFilter(Color.rgb(255, 116, 216))
            view?.toast("Added to Favourites - Don't forget to Save!")
        } else {
            view?.toast("Removed from Favourites - Don't forget to Save!")
            view?.fabMoreFavourite!!.setColorFilter(Color.rgb(255, 255, 255))
        }
    }

    fun doShare() {
        view?.createShareIntent(hillfort)
    }

    fun doAddOrSave(tempHillfort: HillfortModel) {
        hillfort.name = tempHillfort.name
        hillfort.description = tempHillfort.description
        hillfort.visited = tempHillfort.visited
        hillfort.dateVisited = tempHillfort.dateVisited
        hillfort.images = images
        hillfort.rating = tempHillfort.rating

        doAsync {
            if (edit) {
                hillfort.notes = notes!!
                app.hillforts.updateHillfort(hillfort)
            } else {
                app.hillforts.createHillfort(hillfort)
            }
            uiThread {
                view?.finish()
                view?.navigateTo(VIEW.LIST)
            }
        }
    }

    fun doNavigation(): Boolean {
        view?.finish()
        view?.navigateTo(VIEW.NAVIGATOR, 0, "hillfort", hillfort)
        return true
    }

    fun doDelete() {
        doAsync {
            app.hillforts.deleteHillfort(hillfort)
            uiThread {
                view?.finish()
            }
        }
    }

    fun doNext() {
        val hillforts = app.hillforts.findAllHillforts()
        val index = hillforts?.indexOf(hillfort)
        try {
            view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", hillforts?.get(index!!.plus(1)))
        } catch (e: IndexOutOfBoundsException) {
            view?.toast("Next Hillfort is Empty!")
        }
    }

    fun doPrevious() {
        val hillforts = app.hillforts.findAllHillforts()
        val index = hillforts?.indexOf(hillfort)
        try {
            view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", hillforts?.get(index!!.minus(1)))
        } catch (e: IndexOutOfBoundsException) {
            view?.toast("Previous Hillfort is Empty!")
        }
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun doTakePicture() {
        if (checkCameraAndStoragePermissions(view!!))
            view?.let {
                takePicture(view!!, IMAGE_CAPTURE_REQUEST)
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
            newNote.fbId = hillfort.fbId
            doAsync {
                hillfort.notes.add(newNote)
                uiThread {
                    view?.showNotes(notes)
                }
            }
        }
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                images.clear()
                // if multiple images selected
                if (data.clipData != null) {
                    if (data.clipData!!.itemCount > 4) {
                        view?.toast("Exceeded maximum of 4 images")
                    } else {
                        val mClipData = data.clipData
                        var counter = 0
                        while (counter < mClipData!!.itemCount) {
                            val newImage = ImageModel()
                            newImage.uri = mClipData.getItemAt(counter).uri.toString()
                            newImage.fbID = hillfort.fbId
                            newImage.id = Random().nextInt()
                            images.add(newImage)
                            counter++
                        }
                    }
                    // else add single image
                } else {
                    val newImage = ImageModel()
                    newImage.uri = data.data.toString()
                    newImage.fbID = hillfort.fbId
                    newImage.id = Random().nextInt()
                    images.add(newImage)
                }

                hillfort.images = images
                view?.showImages(images)
            }
            LOCATION_REQUEST -> {
                location = data.extras?.getParcelable("location")!!
                hillfort.location = location
                val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
                view?.showUpdatedMap(latLng)
            }
            IMAGE_CAPTURE_REQUEST -> {

                val path = getCurrentImagePath()
                if (path != null) {

                    if (hillfort.images.size >= 4) {
                        view?.toast("Only 4 images allowed!")
                    } else {

                        val newImage = ImageModel()
                        newImage.uri = path
                        newImage.fbID = hillfort.fbId
                        newImage.id = Random().nextInt()

                        hillfort.images.add(newImage)
                        view?.showImages(hillfort.images)
                    }
                }

//                var bitmap: Bitmap = BitmapFactory.decodeFile(imagePath)
//                images.add(bitmap)
            }
        }
    }
}

