package org.wit.hillfortapp.views.stats

import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.models.HillfortModel

class StatsPresenter(val view: StatsView) {

    var app: MainApp = view.application as MainApp

    fun doGetNotes(): Int {
        return app.users.findAllHillfortNotes().size
    }

    fun doGetMostActiveUser(): String {
        val users = app.users.findAll()
        var hillfortsCount = 0
        var topUser = ""

        for (user in users) {
            if (user.hillforts.size > hillfortsCount) {
                hillfortsCount = user.hillforts.size
                topUser = user.username
            }
        }
        return topUser
    }

    fun doGetVisits(): Int {
        val hillforts: ArrayList<HillfortModel>? = app.users.findAllHillforts()
        var totalVisits = 0
        if (hillforts != null) {
            for (hillfort in hillforts) {
                if (hillfort.visited) {
                    totalVisits++
                }
            }
        }
        return totalVisits
    }

    fun doGetImages(): Int {
        val hillforts: ArrayList<HillfortModel>? = app.users.findAllHillforts()
        var totalImages = 0
        if (hillforts != null) {
            for (hillfort in hillforts) {
                totalImages += hillfort.images.size
            }
        }
        return totalImages
    }

    fun doGetUsers(): Int {
        return app.users.findAll().size
    }

    fun doGetHillforts(): Int? {
        return app.users.findAllHillforts()?.size
    }

}