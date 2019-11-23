package org.wit.hillfortapp.views.account

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.activities.LoginActivity
import org.wit.hillfortapp.models.UserModel

class AccountPresenter(val view: AccountView) {

    var app: MainApp = view.application as MainApp

    init {
        view.showAccount(app.activeUser)
    }

    fun doUpdate() {

        val mDialogView = LayoutInflater.from(view).inflate(R.layout.dialog_account, null)
        val builder = AlertDialog.Builder(view)
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
                    .contains("") -> view.toast("Please fill out all fields")
                !isEmailValid(emailField?.text.toString()) -> view.toast("Please enter a valid email")
                else -> {
                    val editedUser = UserModel(
                        app.activeUser.id,
                        usernameField?.text.toString(),
                        emailField?.text.toString(),
                        passwordField?.text.toString(),
                        app.activeUser.hillforts
                    )
                    app.users.update(editedUser)
                    view.showAccount(editedUser)
                    dialog.dismiss()
                }
            }
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun doDelete() {
        val builder = AlertDialog.Builder(view)
        builder.setMessage("Are you sure you want to delete your account?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            app.users.deleteUser(app.activeUser)
            dialog.dismiss()
            view.startActivity(Intent(view, LoginActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun doDeleteHillforts() {
        val builder = AlertDialog.Builder(view)
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


    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

