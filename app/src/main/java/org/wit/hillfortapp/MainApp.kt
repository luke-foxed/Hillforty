package org.wit.hillfortapp

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.*
import org.wit.hillfortapp.models.firebase.HillfortFireStore

class MainApp : Application(), AnkoLogger {

    lateinit var activeUser: UserModel
    lateinit var hillforts: HillfortFireStore

    override fun onCreate() {
        super.onCreate()
        info("App started")
        hillforts = HillfortFireStore(applicationContext)
    }
}