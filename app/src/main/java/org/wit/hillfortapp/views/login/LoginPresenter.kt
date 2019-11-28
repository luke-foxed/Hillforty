package org.wit.hillfortapp.views.login

import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.main.MainView

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    fun doLogin(username: String, password: String) {
        try {
            val user: UserModel = app.users.findOne(username, password)!!
            app.activeUser = user
            view?.startActivity(
                view?.intentFor<MainView>()?.putExtra(
                    "username", username
                )
            )
        } catch (e: Exception) {
            view?.toast("No user found!")
        }
    }
}