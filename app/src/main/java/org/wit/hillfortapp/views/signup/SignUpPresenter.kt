package org.wit.hillfortapp.views.signup

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class SignUpPresenter(view: BaseView) : BasePresenter(view) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun doSignup(user: UserModel) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(view!!) { task ->
                if (task.isSuccessful) {
                    view?.navigateTo(VIEW.LOGIN)
                } else {
                    view?.toast("Sign Up Failed: ${task.exception?.message}")
                }
            }
    }
}