package org.wit.hillfortapp.views.account

import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView

class AccountPresenter(view: BaseView) : BasePresenter(view) {

    init {
        view.showAccount(app.activeUser)
    }

    fun doUpdate(user: UserModel) {
        app.users.update(user)
        view?.showAccount(user)
    }

    fun doDelete() {
        app.users.deleteUser(app.activeUser)
        view?.finish()
    }

    fun doDeleteHillforts() {
        app.users.deleteAllHillforts(app.activeUser)
    }
}

