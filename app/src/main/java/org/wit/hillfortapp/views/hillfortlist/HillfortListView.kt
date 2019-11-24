package org.wit.hillfortapp.views.hillfortlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView
import org.wit.hillfortapp.models.HillfortModel

class HillfortListView : MainView(),
    HillfortListener {

    lateinit var app: MainApp
    private lateinit var presenter: HillfortListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort_list, content_frame)

        app = application as MainApp
        presenter =
            HillfortListPresenter(this)

        val layoutManager = LinearLayoutManager(this)
        hillfortRecyclerView.layoutManager = layoutManager
        hillfortRecyclerView.adapter =
            HillfortAdapter(
                presenter.getHillforts(),
                this
            )
        hillfortRecyclerView.adapter?.notifyDataSetChanged()

        hillfortListFloatingBtn.setOnClickListener {
            presenter.doAddHillfort()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_blank, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        presenter.doEditHillfort(hillfort)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}

