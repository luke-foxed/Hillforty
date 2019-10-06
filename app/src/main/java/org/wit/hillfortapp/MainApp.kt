package org.wit.hillfortapp

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.models.UserMemStore

class MainApp : Application(), AnkoLogger {

    val users = UserMemStore()

    override fun onCreate() {
        super.onCreate()
        info("App started")
    }
}