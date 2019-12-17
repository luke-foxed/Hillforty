package org.wit.hillfortapp.views.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.BaseView
import org.wit.hillfortapp.views.VIEW

class LoginView : BaseView(), AnkoLogger {

    private lateinit var presenter: LoginPresenter

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var loginDividerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = initPresenter(LoginPresenter(this)) as LoginPresenter
        email = findViewById(R.id.loginEmailInput)
        password = findViewById(R.id.loginPasswordInput)
        signupButton = findViewById(R.id.loginSignUpButton)
        loginButton = findViewById(R.id.loginButton)
        loginDividerText = findViewById(R.id.loginDividerText)

        progressBar.visibility = View.GONE

        signupButton.setOnClickListener {
            navigateTo(VIEW.SIGNUP)
            email.text.clear()
            password.text.clear()
        }

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {

        val usernameText = email.text.toString()
        val passwordText = password.text.toString()

        if (listOf(
                usernameText,
                passwordText
            ).contains("")
        ) {
            toast("Please fill out all fields")
        } else {
            presenter.doLogin(usernameText, passwordText)
        }
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        // stop multiple login requests being sent
        loginButton.visibility = View.VISIBLE
        signupButton.visibility = View.VISIBLE
        loginDividerText.visibility = View.VISIBLE
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
        loginButton.visibility = View.GONE
        signupButton.visibility = View.GONE
        loginDividerText.visibility = View.GONE
    }
}