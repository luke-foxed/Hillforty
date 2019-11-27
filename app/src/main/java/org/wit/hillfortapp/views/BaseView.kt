package org.wit.hillfortapp.views

import android.content.Intent
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Note
import org.wit.hillfortapp.views.editlocation.EditLocationView
import org.wit.hillfortapp.views.hillfort.HillfortView
import org.wit.hillfortapp.views.hillfortlist.HillfortListView
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.views.map.HillfortMapsView


val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2

enum class VIEW {
    LOCATION, HILLFORT, MAPS, LIST
}

abstract class BaseView : MainView(), AnkoLogger {

    private var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, HillfortListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.HILLFORT -> intent = Intent(this, HillfortView::class.java)
            VIEW.MAPS -> intent = Intent(this, HillfortMapsView::class.java)
            VIEW.LIST -> intent = Intent(this, HillfortListView::class.java)
        }
        if (key != "") {
            intent.putExtra(key, value)
        }
        startActivityForResult(intent, code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter
        return presenter
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun showHillfort(hillfort: HillfortModel) {}
    open fun showHillforts(hillforts: ArrayList<HillfortModel>) {}
    open fun showNotes(notes: ArrayList<Note>?) {}
    open fun showImages(images: ArrayList<String>?) {}
    open fun showUpdatedMap(latLng: LatLng) {}
    open fun showProgress() {}
    open fun hideProgress() {}

}