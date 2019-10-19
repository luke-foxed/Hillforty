package org.wit.hillfortapp.models

interface HillfortStore {
    fun findAll(): ArrayList<HillfortModel>
    fun findOne(id: Int): HillfortModel
    fun create(user: HillfortModel)
    fun update(user: HillfortModel)
}