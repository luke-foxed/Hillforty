package org.wit.hillfortapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel

class LoginActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp

    private var email: EditText? = null
    private var password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        app = application as MainApp

        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)

        val signupButton = findViewById<Button>(R.id.signupButton)
        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton!!.setOnClickListener { login() }
    }

    private fun login() {

        val emailText = email!!.text.toString()
        val passwordText = password!!.text.toString()

        if (listOf(
                emailText,
                passwordText
            ).contains("")
        ) {
            toast("Please fill out all fields")
        } else {
            try {
                // val user: UserModel = app.users.findOne(emailText, passwordText)
                // toast("Welcome back, ${user.email}")
               //  app.activeUser = user
                startActivity(Intent(this@LoginActivity, HillfortListActivity::class.java))
            } catch (e: Exception) {
                info(e.message)
                toast("No user found!")
            }
        }
    }
}