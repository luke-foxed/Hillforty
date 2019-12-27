package org.wit.hillfortapp.views.stats

import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.views.BasePresenter
import org.wit.hillfortapp.views.BaseView

class StatsPresenter(view: BaseView) : BasePresenter(view) {

    var hillforts: List<HillfortModel> = app.hillforts.findAllHillforts()!!

    fun getAverageRatings(): Double {
        var average = 0.0
        hillforts.forEach {
            average += it.rating
        }
        average /= hillforts.size
        return average
    }

    fun getAllFavourites(): Int {
        var favourites = 0
        hillforts.forEach {
            if (it.isFavourite) {
                favourites++
            }
        }
        return favourites
    }

    fun getAllVisited(): Int {
        var visited = 0
        hillforts.forEach {
            if (it.visited) {
                visited++
            }
        }
        return visited
    }

    fun getAllNotes(): Int {
        var notes = 0
        hillforts.forEach {
            notes += it.notes.size
        }
        return notes
    }

    fun getAllImages(): Int {
        var images = 0
        hillforts.forEach {
            images += it.images.size
        }
        return images
    }

}