package org.wit.hillfortapp.views.signup

import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView

class SignUpPresenter(view: BaseView) : BasePresenter(view) {

    fun doSignup(user: UserModel) {
        app.users.create(user.copy())
    }

    fun doFindUsername(username:String): Boolean {
        return app.users.findUsername(username)
    }
}