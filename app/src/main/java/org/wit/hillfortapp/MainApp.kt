package org.wit.hillfortapp

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.*
import org.wit.hillfortapp.room.UserStoreRoom

class MainApp : Application(), AnkoLogger {

    lateinit var activeUser: UserModel
    lateinit var users: UserStore

    override fun onCreate() {
        super.onCreate()
        info("App started")
        users = UserStoreRoom(applicationContext)
    }
}