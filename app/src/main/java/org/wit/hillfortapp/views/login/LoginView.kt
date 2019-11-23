package org.wit.hillfortapp.views.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.signup.SignUpView

class LoginView : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    private lateinit var presenter: LoginPresenter

    private var username: EditText? = null
    private var password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        app = application as MainApp
        presenter = LoginPresenter(this)

        username = findViewById(R.id.loginUsernameInput)
        password = findViewById(R.id.loginPasswordInput)

        val signUpButton = findViewById<Button>(R.id.loginSignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpView::class.java)
            startActivity(intent)
            username!!.text.clear()
            password!!.text.clear()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton!!.setOnClickListener { login() }
    }

    private fun login() {

        val usernameText = username!!.text.toString()
        val passwordText = password!!.text.toString()

        if (listOf(
                usernameText,
                passwordText
            ).contains("")
        ) {
            toast("Please fill out all fields")
        } else {
            presenter.doLogin(usernameText, passwordText)
            username!!.text.clear()
            password!!.text.clear()
        }
    }
}