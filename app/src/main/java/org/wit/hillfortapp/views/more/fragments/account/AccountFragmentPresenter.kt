package org.wit.hillfortapp.views.more.fragments.account

import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfortapp.views.more.fragments.BaseFragmentPresenter

class AccountFragmentPresenter(private val fragment: AccountFragment) :
    BaseFragmentPresenter(fragment) {

    fun doGetUser(): FirebaseUser? {
        return app.activeUser
    }

    fun doUpdateEmail(email: String) {
        app.activeUser?.updateEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fragment.activity?.toast("User email address updated.")
                    app.hillforts.logout()
                    app.activeUser = null
                    //fragment.startActivity()
                } else {
                    fragment.activity?.toast("Email Change Failed: ${task.exception?.message}")
                }
            }
    }

    fun doUpdatePassword(password: String) {
        app.activeUser?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fragment.activity?.toast("User password updated.")
                    app.hillforts.logout()
                    app.activeUser = null
//                    fragment.activity?.navigateTo(VIEW.LOGIN)
                } else {
                    fragment.activity?.toast("Password Change Failed: ${task.exception?.message}")
                }
            }
    }

    fun doDelete() {
        app.hillforts.deleteUser(app.activeUser!!)
//        fragment.activity?.navigateTo(VIEW.LOGIN)

    }


    fun doDeleteHillforts() {
        app.hillforts.deleteAllHillforts()
    }
}