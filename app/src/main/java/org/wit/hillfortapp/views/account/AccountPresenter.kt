package org.wit.hillfortapp.views.account

import org.jetbrains.anko.toast
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class AccountPresenter(view: BaseView) : BasePresenter(view) {

    init {
        view.showAccount(app.activeUser!!)
    }

    fun doUpdateEmail(email: String) {
        app.activeUser?.updateEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    view?.toast("User email address updated.")
                    app.hillforts.logout()
                    app.activeUser = null
                    view?.navigateTo(VIEW.LOGIN)
                } else {
                    view?.toast("Email Change Failed: ${task.exception?.message}")
                }
            }
    }

    fun doUpdatePassword(password: String) {
        app.activeUser?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    view?.toast("User password updated.")
                    app.hillforts.logout()
                    app.activeUser = null
                    view?.navigateTo(VIEW.LOGIN)
                } else {
                    view?.toast("Password Change Failed: ${task.exception?.message}")
                }
            }
    }

    fun doDelete() {
       // app.users.deleteUser(app.activeUser)
        view?.navigateTo(VIEW.LOGIN)
    }

    fun doDeleteHillforts() {
       // app.users.deleteAllHillforts(app.activeUser.id)
    }
}

