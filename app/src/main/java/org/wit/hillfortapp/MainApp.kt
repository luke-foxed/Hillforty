package org.wit.hillfortapp

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.*

class MainApp : Application(), AnkoLogger {

    lateinit var activeUser: UserModel
    lateinit var hillforts: HillfortStore
    lateinit var users: UserStore

    override fun onCreate() {
        super.onCreate()
        info("App started")
        hillforts = HillfortMemStore()
        // users = UserMemStore()
        users = UserJSONStore(applicationContext)

    }
}