package org.wit.hillfortapp.models

interface HillfortStore {
    fun findAll(): List<HillfortModel>
    fun findOne(id: Long): HillfortModel
    fun create(user: HillfortModel)
    fun update(user: HillfortModel)
}