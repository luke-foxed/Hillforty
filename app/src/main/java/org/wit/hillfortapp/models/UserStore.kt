package org.wit.hillfortapp.models

interface UserStore {
    fun findAll(): ArrayList<UserModel>
    fun findOne(email: String, password: String): UserModel
    fun create(user: UserModel)
    fun update(user: UserModel)

    // Hillfort functionality

    fun findAllHillforts(activeUser: UserModel): ArrayList<HillfortModel>
    fun findOneHillfort(hillfortID: Int, activeUser: UserModel): HillfortModel
    fun createHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun updateHillfort(hillfort: HillfortModel, activeUser: UserModel)
}