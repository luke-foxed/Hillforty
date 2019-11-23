package org.wit.hillfortapp.views.signup

import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.models.UserModel

class SignUpPresenter(val view: SignUpView) {

    var app: MainApp = view.application as MainApp

    fun doSignup(user: UserModel) {
        app.users.create(user.copy())
    }

    fun doFindUsername(username:String): Boolean {
        return app.users.findUsername(username)
    }
}