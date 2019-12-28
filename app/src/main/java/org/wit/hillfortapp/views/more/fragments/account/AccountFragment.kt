package org.wit.hillfortapp.views.more.fragments.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_account.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.login.LoginView
import org.wit.hillfortapp.views.more.fragments.BaseFragment

class AccountFragment : BaseFragment(), AnkoLogger {

    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var presenter: AccountFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_account, container, false)
        presenter = initPresenter(AccountFragmentPresenter(this)) as AccountFragmentPresenter

        view?.accountEmail?.text = presenter.doGetUser()?.email

        view.accountChangeEmail.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(activity).inflate(R.layout.dialog_account_email, null)
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Enter A New Email (You Will Be Logged Out): ")
            builder.setView(mDialogView)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            val updateBtn = dialog.findViewById(R.id.accountDialogUpdate) as Button
            val cancelBtn = dialog.findViewById(R.id.accountDialogCancel) as Button
            val emailField = dialog.findViewById(R.id.accountDialogEmail) as? EditText

            updateBtn.setOnClickListener {
                when {
                    emailField?.text.toString() == "" -> activity?.toast("Please fill out all fields")
                    !isEmailValid(emailField?.text.toString()) -> activity?.toast("Please enter a valid email")
                    else -> {
                        presenter.doUpdateEmail(emailField?.text.toString())
                    }
                }
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        view.accountChangePassword.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(activity).inflate(R.layout.dialog_account_password, null)
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Enter A New Password (You Will Be Logged Out): ")
            builder.setView(mDialogView)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            val updateBtn = dialog.findViewById(R.id.accountDialogPasswordUpdate) as Button
            val cancelBtn = dialog.findViewById(R.id.accountDialogPasswordCancel) as Button
            val password = dialog.findViewById(R.id.accountDialogPassword) as? EditText
            val passwordConfirm =
                dialog.findViewById(R.id.accountDialogPasswordConfirm) as? EditText

            updateBtn.setOnClickListener {
                when {
                    listOf(
                        password?.text.toString(),
                        passwordConfirm?.text.toString()
                    ).contains(
                        ""
                    ) -> activity?.toast("Please fill out all fields")
                    password!!.text.toString() != passwordConfirm!!.text.toString() -> activity?.toast(
                        "Passwords do not match"
                    )
                    else -> {
                        presenter.doUpdatePassword(password.text.toString())
                    }
                }
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

        }

        view.accountDeleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Are you sure you want to delete your account?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                presenter.doDelete()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        view.accountDeleteHillfortsBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Are you sure you want to delete all your hillforts?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                presenter.doDeleteHillforts()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        return view
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}

