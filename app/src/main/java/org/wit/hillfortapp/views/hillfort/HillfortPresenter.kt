package org.wit.hillfortapp.views.hillfort

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
import java.util.*

class HillfortPresenter(view: BaseView) : BasePresenter(view) {

    private var hillfort = HillfortModel()

    var map: GoogleMap? = null
    val locationRequest = createDefaultLocationRequest()

    private var notes: ArrayList<NoteModel> = arrayListOf()
    private var images: ArrayList<ImageModel> = arrayListOf()

    private var edit = false

    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2
    private val IMAGE_CAPTURE_REQUEST = 3

    private var locationService: FusedLocationProviderClient =
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
            locationUpdate(it.latitude, it.longitude)
        }
    }


    @SuppressLint("MissingPermission")
    fun doRestartLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    val l = locationResult.locations.last()
                    locationUpdate(l.latitude, l.longitude)
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun doClickNote(noteModel: NoteModel) {
        view?.alert("${noteModel.title}\n\n${noteModel.content}")?.show()
    }

    fun doFavourite() {
        if (hillfort.fbId == "") {
            view?.toast("Please Finish Creating The Hillfort")
        } else {
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
    }

    fun doShare() {
        if (edit) {
            view?.createShareIntent(hillfort)
        } else {
            view?.toast("Please finish creating this Hillfort first!")
        }
    }

    fun doAddOrSave(tempHillfort: HillfortModel) {
        hillfort.name = tempHillfort.name
        hillfort.description = tempHillfort.description
        hillfort.visited = tempHillfort.visited
        hillfort.dateVisited = tempHillfort.dateVisited
        hillfort.images = images
        hillfort.notes = notes
        hillfort.rating = tempHillfort.rating

        doAsync {
            if (edit) {
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
        if (edit) {
            view?.navigateTo(VIEW.NAVIGATOR, 0, "hillfort", hillfort)
        } else {
            view?.toast("Please finish creating this Hillfort first!")
        }
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

    fun doChooseCover(index: Int) {
        when {
            hillfort.images.size == 0 -> {
                view?.toast("No Images Exist!")
            }
            !edit -> {
                view?.toast("Please Save The Hillfort First")
            }
            else -> {
                // swap indexes so chosen image appears in recycle-view (i.e. cover image)
                hillfort.images[0] =
                    hillfort.images[index].also { hillfort.images[index] = hillfort.images[0] }
                view?.showImages(hillfort.images)
                view?.toast("Above Image Selected As Cover - Don't Forget To Save!")
            }
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
        val newNote = NoteModel()
        newNote.title = title
        newNote.content = content
        newNote.id = notes.size.plus(1)
        newNote.fbId = hillfort.fbId
        doAsync {
            notes.add(newNote)
            hillfort.notes = notes
            uiThread {
                view?.showNotes(notes)
            }
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        locationUpdate(hillfort.location.lat, hillfort.location.lng)
    }

    fun locationUpdate(lat: Double, lng: Double) {

        hillfort.location.lat = lat
        hillfort.location.lng = lng
        hillfort.location.zoom = 15f

        map?.clear()
        map?.uiSettings?.isZoomControlsEnabled = true
        val options = MarkerOptions().title(hillfort.name)
            .position(LatLng(hillfort.location.lat, hillfort.location.lng))
        map?.addMarker(options)
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    hillfort.location.lat,
                    hillfort.location.lng
                ), hillfort.location.zoom
            )
        )
        view?.showUpdatedMap(LatLng(hillfort.location.lat, hillfort.location.lng))
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
                locationUpdate(location.lat, location.lng)
            }
            IMAGE_CAPTURE_REQUEST-> {

                val path = getCurrentImagePath()
                if (path != null) {

                    if (hillfort.images.size >= 4) {
                        view?.toast("Only 4 images allowed!")
                    } else {
                        
                        val newImage = ImageModel()
                        newImage.uri = path
                        newImage.fbID = hillfort.fbId
                        newImage.id = Random().nextInt()
                        images.add(newImage)

                        if(resultCode == RESULT_OK) {
                            hillfort.images = images
                            view?.showImages(images)
                        }
                    }
                }
            }
        }
    }
}

