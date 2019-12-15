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
import java.util.*
import kotlin.collections.ArrayList

class HillfortListPresenter(view: BaseView) : BasePresenter(view) {

    private var currentHillforts: ArrayList<HillfortModel> = arrayListOf()

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
                    currentHillforts = hillforts as ArrayList<HillfortModel>
                    view?.showHillforts(hillforts)
                }
            }
        }
    }

    fun doSortFavourite() {
        val favourites = app.hillforts.findAllFavourites()
        if (favourites != null) {
            currentHillforts = favourites
            view?.showHillforts(favourites as List<HillfortModel>)
        }
    }

    fun doSearch(query: String) {
        val foundHillforts = app.hillforts.findHillfortsByName(query)
        view?.info(foundHillforts)
        if (foundHillforts != null) {
            currentHillforts = foundHillforts
            view?.showHillforts(foundHillforts as List<HillfortModel>)
        }
    }

    fun doSortByRating() {
        val ratedHillforts = app.hillforts.sortByRating()
        if (ratedHillforts != null) {
            view?.showHillforts(ratedHillforts)
        }
    }

    fun doAscendingOrder() {
        view?.showHillforts(currentHillforts)
    }

    fun doDescendingOrder() {
        view?.showHillforts(currentHillforts.asReversed())
    }

    // TODO --> Refactor 'show all hillforts map' to contain only active user hillforts on map

}