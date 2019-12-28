package org.wit.hillfortapp.views.more.fragments.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_stats.view.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.more.fragments.BaseFragment

class StatsFragment : BaseFragment(), AnkoLogger {

    companion object {
        fun newInstance() = StatsFragment()
    }

    private lateinit var presenter: StatsFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        presenter = initPresenter(StatsFragmentPresenter(this)) as StatsFragmentPresenter

        // set stats
        view.statsHillfortsNumber.text = presenter.hillforts.size.toString()
        view.statsImagesNumber.text = presenter.getAllImages().toString()
        view.statsVisitedNumber.text = presenter.getAllVisited().toString()
        view.statsNotesNumber.text = presenter.getAllNotes().toString()
        view.statsHillfortRating.text = presenter.getAverageRatings().toString()
        view.statsFavourites.text = presenter.getAllFavourites().toString()

        return view
    }
}