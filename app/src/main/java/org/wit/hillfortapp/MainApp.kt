package org.wit.hillfortapp

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.*
import org.wit.hillfortapp.models.firebase.HillfortFireStore

class MainApp : Application(), AnkoLogger {

    lateinit var hillforts: HillfortStore
    var activeUser: FirebaseUser? = null

    override fun onCreate() {
        super.onCreate()
        info("App started")
        hillforts = HillfortFireStore(applicationContext)
    }
}