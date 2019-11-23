package org.wit.hillfortapp.views.login

import android.content.Intent
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.activities.MainActivity
import org.wit.hillfortapp.models.UserModel

class LoginPresenter(val view: LoginView) {

    var app: MainApp = view.application as MainApp

    fun doLogin(username: String, password: String) {
        try {
            val user: UserModel = app.users.findOne(username, password)!!
            app.activeUser = user
            view.startActivity(Intent(view, MainActivity::class.java))
        } catch (e: Exception) {
            view.toast("No user found!")
        }
    }
}