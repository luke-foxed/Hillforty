package org.wit.hillfortapp.views.signup

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class SignUpPresenter(view: BaseView) : BasePresenter(view) {

    fun doSignup(user: UserModel) {
        doAsync {
            app.users.create(user.copy())
            uiThread {
                view?.navigateTo(VIEW.LOGIN)
            }
        }
    }

    fun doFindUsername(username:String): Boolean {
        var foundUsername = false
        doAsync {
            foundUsername = app.users.findUsername(username)
        }
        return foundUsername
    }
}