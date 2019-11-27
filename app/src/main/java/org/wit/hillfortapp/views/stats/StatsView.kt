package org.wit.hillfortapp.views.stats

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stats.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView

class StatsView : MainView(), AnkoLogger {

    // lateinit var app: MainApp
    private lateinit var presenter: StatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_stats, content_frame)

        app = application as MainApp
        presenter = StatsPresenter(this)

        // set stats
        statsUsersNumber.text = presenter.doGetUsers().toString()
        statsHillfortsNumber.text = presenter.doGetHillforts().toString()
        statsImagesNumber.text = presenter.doGetImages().toString()
        statsVisitedNumber.text = presenter.doGetVisits().toString()
        statsNotesNumber.text = presenter.doGetNotes().toString()
        statsTopUser.text = presenter.doGetMostActiveUser().toUpperCase()
    }
}