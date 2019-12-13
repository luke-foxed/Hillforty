package org.wit.hillfortapp.views

import android.content.Intent
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.ImageModel
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.editlocation.EditLocationView
import org.wit.hillfortapp.views.hillfort.HillfortView
import org.wit.hillfortapp.views.hillfortlist.HillfortListView
import org.wit.hillfortapp.views.login.LoginView
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.views.map.HillfortMapsView
import org.wit.hillfortapp.views.signup.SignUpView


val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2

enum class VIEW {
    LOCATION, HILLFORT, MAPS, LIST, SIGNUP, MAIN, LOGIN
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
            VIEW.SIGNUP -> intent = Intent(this, SignUpView::class.java)
            VIEW.MAIN -> intent = Intent(this, MainView::class.java)
            VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
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
    open fun showHillforts(hillforts: List<HillfortModel>) {}
    open fun showNotes(notes: ArrayList<NoteModel>?) {}
    open fun showImages(images: ArrayList<ImageModel>) {}
    open fun showUpdatedMap(latLng: LatLng) {}
    open fun showAccount(user: UserModel) {}
    open fun showProgress() {}
    open fun hideProgress() {}

}