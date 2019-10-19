package org.wit.hillfortapp.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastHillfortId = 0

internal fun getHillfortId(): Int {
    return lastHillfortId++
}

class HillfortMemStore : HillfortStore, AnkoLogger {
    val hillforts = ArrayList<HillfortModel>()

    override fun findAll(): ArrayList<HillfortModel> {
        return hillforts
    }

    override fun findOne(id: Int): HillfortModel {
        return hillforts.single { hillfort ->
            hillfort.id == id
        }
    }

    override fun create(hillfort: HillfortModel) {
        hillfort.id = getId()
        hillforts.add(hillfort)
        logAll()
    }

    override fun update(hillfort: HillfortModel) {
        val foundHillfort: HillfortModel? = hillforts.find { h -> h.id == hillfort.id }
        if (foundHillfort != null) {
            foundHillfort.id = hillfort.id
            foundHillfort.description = hillfort.description
            foundHillfort.location = hillfort.location
            foundHillfort.notes = hillfort.notes
            foundHillfort.visited = hillfort.visited
            foundHillfort.dateVisited = hillfort.dateVisited
            foundHillfort.image = hillfort.image
        }
    }

    private fun logAll() {
        hillforts.forEach { info("${it}") }
    }
}