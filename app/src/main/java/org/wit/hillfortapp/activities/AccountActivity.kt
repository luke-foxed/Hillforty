package org.wit.hillfortapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_main.*
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

        acccount_delete.setOnClickListener {

            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Are you sure you want to delete your account?")
            builder.setPositiveButton("Yes") { dialog, which ->
                app.users.deleteUser(app.activeUser)
                startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
            }
            builder.setNegativeButton("No") { dialog, which ->
                // do nothing
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        account_delete_hillforts.setOnClickListener {
            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Are you sure you want to delete all your hillforts?")
            builder.setPositiveButton("Yes") { dialog, which ->
                app.users.deleteAllHillforts(app.activeUser)
            }
            builder.setNegativeButton("No") { dialog, which ->
                // do nothing
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        account_edit_account.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account, null)
            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setMessage("Enter new account details: ")
            builder.setView(mDialogView)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            val updateBtn = dialog.findViewById(R.id.dialogUpdate) as Button
            val cancelBtn = dialog.findViewById(R.id.dialogCancel) as Button
            val emailField = dialog.findViewById(R.id.dialogEmail) as? EditText
            val passwordField = dialog.findViewById(R.id.dialogPassword) as? EditText

            updateBtn.setOnClickListener {
                when {
                    listOf(emailField?.text.toString(), passwordField?.text.toString())
                        .contains("") -> toast("No changes made!")
                    !isEmailValid(emailField?.text.toString()) -> toast("Please enter a valid email")
                    else -> {
                        val newUser = UserModel(
                            app.activeUser.id,
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
        account_email.text = app.activeUser.email
        account_password.text = app.activeUser.password
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}