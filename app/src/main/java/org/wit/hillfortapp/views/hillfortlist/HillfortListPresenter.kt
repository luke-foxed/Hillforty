package org.wit.hillfortapp.views.hillfortlist

import org.jetbrains.anko.startActivityForResult
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
//        view?.showHillforts(app.activeUser.hillforts)
    }

    // TODO --> Refactor 'show all hillforts map' to contain only active user hillforts on map

}