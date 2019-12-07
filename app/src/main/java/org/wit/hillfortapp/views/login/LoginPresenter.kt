package org.wit.hillfortapp.views.login

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                doAsync {
                    val user: UserModel = app.users.findOne(email, password)!!
                    app.activeUser = user
                    uiThread {
                        view?.navigateTo(VIEW.MAIN, 0, "user", user)
                    }
                }
            } else {
                view?.toast("error")
            }
        }
    }
}