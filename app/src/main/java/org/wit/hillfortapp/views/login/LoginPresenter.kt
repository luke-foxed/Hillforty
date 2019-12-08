package org.wit.hillfortapp.views.login

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.models.firebase.HillfortFireStore
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fireStore: HillfortFireStore? = null

    init {
        if (app.hillforts is HillfortFireStore) {
            fireStore = app.hillforts as HillfortFireStore
        }
    }

//    fun doLogin(username: String, password: String) {
//
//        doAsync {
//            try {
//                val user: UserModel = app.users.findOne(username, password)!!
//                app.activeUser = user
//                uiThread {
//                    view?.navigateTo(VIEW.MAIN, 0, "user", user)
//                }
//            } catch (e: Exception) {
//                uiThread {
//                    view?.toast("No user found!")
//                }
//            }
//        }
//    }

    fun doLogin(email: String, password: String) {
        view?.showProgress()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                view?.info(fireStore)
                if (fireStore != null) {
                    fireStore!!.fetchHillforts {
                        view?.hideProgress()
                        view?.navigateTo(VIEW.MAIN)
                    }
                } else {
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