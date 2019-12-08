package org.wit.hillfortapp.views.login

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.models.firebase.HillfortFireStore
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW
import org.wit.hillfortapp.views.main.MainView

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fireStore: HillfortFireStore? = null

    fun doLogin(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                view?.info("was successful")
                if (fireStore != null) {
                    view?.info("NOT NULL DB")
                    fireStore!!.fetchHillforts {
                        view?.info("MADE IT HERE")
                        view?.hideProgress()
                        view?.navigateTo(VIEW.MAIN)
                    }
                } else {
                    view?.info("NULL DB")
                    view?.hideProgress()
                    view?.navigateTo(VIEW.MAIN)
                }
            } else {
                view?.hideProgress()
                view?.toast("Sign Up Failed: ${task.exception?.message}")
            }
        }
    }
}