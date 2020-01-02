package org.wit.hillfortapp.models

import com.google.firebase.auth.FirebaseUser

interface HillfortStore {

    fun findAllHillforts(): List<HillfortModel>?
    fun findOneHillfort(hillfortID: Int): HillfortModel?
    fun findHillfortsByName(name:String): ArrayList<HillfortModel>?
    fun createHillfort(hillfort: HillfortModel)
    fun updateHillfort(hillfort: HillfortModel)
    fun deleteHillfort(hillfort: HillfortModel)
    fun deleteAllHillforts()
    fun toggleFavourite(hillfort: HillfortModel)
    fun sortedByFavourite(): List<HillfortModel>?
    fun sortByRating(): List<HillfortModel>?
    fun sortByVisit(): List<HillfortModel>?

    // user functions
    fun deleteUser(user:FirebaseUser)
    fun logout()


}