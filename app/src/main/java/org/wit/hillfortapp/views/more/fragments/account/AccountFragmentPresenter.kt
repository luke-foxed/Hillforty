package org.wit.hillfortapp.views.more.fragments.account

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.toast
import org.wit.hillfortapp.views.login.LoginView
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
                    fragment.startActivity(Intent(fragment.activity, LoginView::class.java))
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
                    fragment.startActivity(Intent(fragment.activity, LoginView::class.java))
                } else {
                    fragment.activity?.toast("Password Change Failed: ${task.exception?.message}")
                }
            }
    }

    fun doDelete() {
        app.hillforts.deleteUser(app.activeUser!!)
        fragment.startActivity(Intent(fragment.activity, LoginView::class.java))

    }


    fun doDeleteHillforts() {
        app.hillforts.deleteAllHillforts()
    }
}