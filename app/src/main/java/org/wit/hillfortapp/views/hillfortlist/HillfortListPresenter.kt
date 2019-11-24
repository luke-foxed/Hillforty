package org.wit.hillfortapp.views.hillfortlist

import org.wit.hillfortapp.views.hillfort.HillfortView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.hillfortlist.HillfortListView

class HillfortListPresenter(val view: HillfortListView) {

    var app: MainApp = view.application as MainApp

    fun getHillforts() = app.activeUser.hillforts

    fun doAddHillfort() {
        view.startActivityForResult<HillfortView>(0)
    }

    fun doEditHillfort(hillfort: HillfortModel) {
        view.startActivityForResult(view.intentFor<HillfortView>().putExtra("hillfort_edit", hillfort), 0)
    }

    // TODO --> Refactor 'show all hillforts map' to contain only active user hillforts on map

}