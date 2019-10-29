package org.wit.hillfortapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel

class LoginActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp

    private var username: EditText? = null
    private var password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        app = application as MainApp

        username = findViewById(R.id.loginUsernameInput)
        password = findViewById(R.id.loginPasswordInput)

        val signUpButton = findViewById<Button>(R.id.loginSignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
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
            try {
                val user: UserModel = app.users.findOne(usernameText, passwordText)!!
                app.activeUser = user
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                username!!.text.clear()
                password!!.text.clear()
            } catch (e: Exception) {
                toast("No user found!")
            }
        }
    }
}