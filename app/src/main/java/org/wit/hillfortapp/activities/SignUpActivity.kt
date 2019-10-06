package org.wit.hillfortapp.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.wit.hillfortapp.R
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.models.UserModel

class SignUpActivity : AppCompatActivity(), AnkoLogger {

    var user = UserModel()
    lateinit var app: MainApp

    private var email: EditText? = null
    private var password: EditText? = null
    private var password2: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        app = application as MainApp

        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        password2 = findViewById(R.id.passwordInput2)

        signupButton!!.setOnClickListener { signUp() }
    }

    private fun signUp() {
        val emailText = email!!.text.toString()
        val password1Text = password!!.text.toString()
        val password2Text = password2!!.text.toString()

        if (listOf(
                emailText,
                password1Text,
                password2Text
            ).contains("")
        ) {
            toast("Please fill out all fields")
        }

        if (password1Text != password2Text) {
            toast("Passwords do not match")
        } else {
            user.email = emailText
            user.password = password1Text
            app.users.create(user.copy())
            //  app.users.export(this.applicationContext)
            toast("Account created!")
        }
    }
}