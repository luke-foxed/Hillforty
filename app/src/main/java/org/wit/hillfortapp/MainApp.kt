package org.wit.hillfortapp

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.*
import org.wit.hillfortapp.models.firebase.HillfortFireStore

class MainApp : Application(), AnkoLogger {

    lateinit var hillforts: HillfortStore

    override fun onCreate() {
        super.onCreate()
        info("App started")
        hillforts = HillfortFireStore(applicationContext)
    }
}