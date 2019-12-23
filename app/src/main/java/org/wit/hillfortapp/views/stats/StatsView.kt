package org.wit.hillfortapp.views.stats

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stats.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.BaseView

class StatsView : BaseView(), AnkoLogger {

    private lateinit var presenter: StatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_stats, content_frame)

        presenter = StatsPresenter(this)

        // set stats
        statsHillfortsNumber.text = presenter.hillforts.size.toString()
        statsImagesNumber.text = presenter.getAllImages().toString()
        statsVisitedNumber.text = presenter.getAllVisited().toString()
        statsNotesNumber.text = presenter.getAllNotes().toString()
        statsHillfortRating.text = presenter.getAverageRatings().toString()
        statsFavourites.text = presenter.getAllFavourites().toString()
    }
}