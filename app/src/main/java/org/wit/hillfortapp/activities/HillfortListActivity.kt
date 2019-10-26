package org.wit.hillfortapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.adapters.HillfortAdapter
import org.wit.hillfortapp.adapters.HillfortListener
import org.wit.hillfortapp.models.HillfortModel

class HillfortListActivity : MainActivity(), HillfortListener {


    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_hillfort_list, content_frame)

        app = application as MainApp
        val layoutManager = LinearLayoutManager(this)
        hillfortRecyclerView.layoutManager = layoutManager
        hillfortRecyclerView.adapter =
            HillfortAdapter(app.activeUser.hillforts, this)
        loadHillforts()

        hillfortListFloatingBtn.setOnClickListener {
            startActivity(Intent(this@HillfortListActivity, HillfortActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_blank, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        startActivityForResult(intentFor<HillfortActivity>().putExtra("hillfort_edit", hillfort), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadHillforts()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadHillforts() {
        showHillforts(app.activeUser.hillforts)
    }

    private fun showHillforts(hillforts: List<HillfortModel>) {
        hillfortRecyclerView.adapter = HillfortAdapter(hillforts, this)
        hillfortRecyclerView.adapter?.notifyDataSetChanged()
    }
}

