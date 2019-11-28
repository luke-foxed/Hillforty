package org.wit.hillfortapp.views.login

import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW
import org.wit.hillfortapp.views.main.MainView

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    fun doLogin(username: String, password: String) {
        try {
            val user: UserModel = app.users.findOne(username, password)!!
            app.activeUser = user
            view?.navigateTo(VIEW.MAIN, 0,"user", user)
        } catch (e: Exception) {
            view?.toast("No user found!")
        }
    }
}