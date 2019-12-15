package org.wit.hillfortapp.views.hillfortlist

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW
import org.wit.hillfortapp.views.hillfort.HillfortView

class HillfortListPresenter(view: BaseView) : BasePresenter(view) {

    fun doAddHillfort() {
        view?.startActivityForResult<HillfortView>(0)
    }

    fun doEditHillfort(hillfort: HillfortModel) {
        view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", hillfort)
    }

    fun loadHillforts() {
        doAsync {
            val hillforts = app.hillforts.findAllHillforts()
            view?.info(hillforts)
            uiThread {
                if (hillforts != null) {
                    view?.showHillforts(hillforts)
                }
            }
        }
    }

    fun doSortFavourite() {
        val favourites = app.hillforts.findAllFavourites()
        view?.showHillforts(favourites as List<HillfortModel>)
    }

    // TODO --> Refactor 'show all hillforts map' to contain only active user hillforts on map

}