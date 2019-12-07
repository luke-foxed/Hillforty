package org.wit.hillfortapp.views.login

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    fun doLogin(username: String, password: String) {

        doAsync {
            try {
                val user: UserModel = app.users.findOne(username, password)!!
                app.activeUser = user
                uiThread {
                    view?.navigateTo(VIEW.MAIN, 0, "user", user)
                }
            } catch (e: Exception) {
                uiThread {
                    view?.toast("No user found!")
                }
            }
        }
    }
}