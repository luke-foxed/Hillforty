package org.wit.hillfortapp.views.hillfortlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.BaseView


class HillfortListView : BaseView(),
    HillfortListener {

    private lateinit var presenter: HillfortListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort_list, content_frame)

        presenter = initPresenter(HillfortListPresenter(this)) as HillfortListPresenter

        val layoutManager = LinearLayoutManager(this)
        hillfortRecyclerView.layoutManager = layoutManager
        presenter.loadHillforts()

        hillfortListFloatingBtn.setOnClickListener {
            presenter.doAddHillfort()
        }
    }

    override fun showHillforts(hillforts: List<HillfortModel>) {
        hillfortRecyclerView.adapter = HillfortListAdapter(hillforts, this)
        hillfortRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort_list, menu)
        toolbar.overflowIcon = getDrawable(R.drawable.ic_filter)

        val item = menu!!.findItem(R.id.action_search)
        val searchView: SearchView = item.actionView as SearchView

        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "Enter A Name..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(currentText: String): Boolean {
                presenter.doSearch(currentText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.doSearch(query)
                return true
            }
        })

        searchView.setOnCloseListener {
            presenter.loadHillforts()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        presenter.doEditHillfort(hillfort)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadHillforts()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sortByFavourite -> {
                presenter.doSortFavourite()
                item.isChecked = true
            }

            R.id.sortByRating -> {
                presenter.doSortByRating()
                item.isChecked = true
            }

            R.id.resetView -> {
                presenter.loadHillforts()
            }

            R.id.sortAscending -> {
                presenter.doAscendingOrder()
            }

            R.id.sortDescending -> {
                presenter.doDescendingOrder()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

