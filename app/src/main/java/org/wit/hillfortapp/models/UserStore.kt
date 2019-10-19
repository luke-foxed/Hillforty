package org.wit.hillfortapp.models

interface UserStore {
    fun findAll(): ArrayList<UserModel>
    fun findOne(email: String, password: String): UserModel
    fun create(user: UserModel)
    fun update(user: UserModel)
}