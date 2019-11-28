package org.wit.hillfortapp.views.signup

import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.BaseView

class SignUpView : BaseView(), AnkoLogger {

    private var user = UserModel()
    private lateinit var presenter: SignUpPresenter

    private var username: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var password2: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        presenter = SignUpPresenter(this)

        username = findViewById(R.id.signUpUsernameInput)
        email = findViewById(R.id.signUpEmailInput)
        password = findViewById(R.id.signUpPasswordInput)
        password2 = findViewById(R.id.signUpPasswordInput2)

        signUpButton.setOnClickListener { signUp() }
    }

    private fun signUp() {
        val usernameText = username?.text.toString().trim()
        val emailText = email?.text.toString().trim()
        val password1Text = password?.text.toString().trim()
        val password2Text = password2?.text.toString().trim()

        if (!validationCheck(usernameText, emailText, password1Text, password2Text)) {
            user.username = usernameText
            user.email = emailText
            user.password = password1Text
            presenter.doSignup(user)
            toast("Account created!")
        }
    }

    private fun validationCheck(
        usernameText: String,
        emailText: String,
        password1Text: String,
        password2Text: String
    ): Boolean {

        var hasErrors = false

        when {
            listOf(emailText, password1Text, password2Text, usernameText).contains("") -> {
                toast("Please fill out all fields")
                hasErrors = true
            }
            password1Text != password2Text -> {
                toast("Passwords do not match")
                hasErrors = true
            }

            presenter.doFindUsername(usernameText) -> {
                toast("This username already exists")
                hasErrors = true
            }

            !isEmailValid(emailText) -> {
                toast("Please enter a valid email")
                hasErrors = true
            }
        }
        return hasErrors
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}