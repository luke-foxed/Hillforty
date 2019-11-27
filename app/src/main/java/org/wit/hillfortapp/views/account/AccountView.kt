package org.wit.hillfortapp.views.account

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.models.UserModel

class AccountView : MainView(), AnkoLogger {

   // lateinit var app: MainApp
    private lateinit var presenter: AccountPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_account, content_frame)

        app = application as MainApp
        presenter = AccountPresenter(this)

        accountEditBtn.setOnClickListener{
            presenter.doUpdate()
        }

        accountDeleteBtn.setOnClickListener {
            presenter.doDelete()
        }

        accountDeleteHillfortsBtn.setOnClickListener {
            presenter.doDeleteHillforts()
        }

        accountEditBtn.setOnClickListener {
            presenter.doUpdate()
        }
    }

    fun showAccount(user: UserModel) {
        accountUsername.text = user.username
        accountEmail.text = user.email
        accountPassword.text = user.password
    }
}