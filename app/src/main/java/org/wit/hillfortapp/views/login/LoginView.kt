package org.wit.hillfortapp.views.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class LoginView : BaseView(), AnkoLogger {

    private lateinit var presenter: LoginPresenter

    private var email: EditText? = null
    private var password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = initPresenter(LoginPresenter(this)) as LoginPresenter

        email = findViewById(R.id.loginEmailInput)
        password = findViewById(R.id.loginPasswordInput)

        val signUpButton = findViewById<Button>(R.id.loginSignUpButton)
        signUpButton.setOnClickListener {
            navigateTo(VIEW.SIGNUP)
            email!!.text.clear()
            password!!.text.clear()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton!!.setOnClickListener { login() }
    }

    private fun login() {

        val usernameText = email!!.text.toString()
        val passwordText = password!!.text.toString()

        if (listOf(
                usernameText,
                passwordText
            ).contains("")
        ) {
            toast("Please fill out all fields")
        } else {
            presenter.doLogin(usernameText, passwordText)
            //email!!.text.clear()
            //password!!.text.clear()
        }
    }
}