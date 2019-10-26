package org.wit.hillfortapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel

class AccountActivity : MainActivity(), AnkoLogger {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_account, content_frame)
        app = application as MainApp

        setUserDetails()

        accountDeleteBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Are you sure you want to delete your account?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                app.users.deleteUser(app.activeUser)
                dialog.dismiss()
                startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        accountDeleteHillfortsBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Are you sure you want to delete all your hillforts?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                app.users.deleteAllHillforts(app.activeUser)
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        accountEditBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account, null)
            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Enter new account details: ")
            builder.setView(mDialogView)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            val updateBtn = dialog.findViewById(R.id.accountDialogUpdate) as Button
            val cancelBtn = dialog.findViewById(R.id.accountDialogCancel) as Button
            val usernameField = dialog.findViewById(R.id.accountDialogUsername) as? EditText
            val emailField = dialog.findViewById(R.id.accountDialogEmail) as? EditText
            val passwordField = dialog.findViewById(R.id.accountDialogPassword) as? EditText

            updateBtn.setOnClickListener {
                when {
                    listOf(emailField?.text.toString(), passwordField?.text.toString())
                        .contains("") -> toast("Please fill out all fields")
                    !isEmailValid(emailField?.text.toString()) -> toast("Please enter a valid email")
                    else -> {
                        val newUser = UserModel(
                            app.activeUser.id,
                            usernameField?.text.toString(),
                            emailField?.text.toString(),
                            passwordField?.text.toString(),
                            app.activeUser.hillforts
                        )
                        app.users.update(newUser)
                        setUserDetails()
                        dialog.dismiss()
                    }
                }
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun setUserDetails() {
        accountUsername.text = app.activeUser.username
        accountEmail.text = app.activeUser.email
        accountPassword.text = app.activeUser.password
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}