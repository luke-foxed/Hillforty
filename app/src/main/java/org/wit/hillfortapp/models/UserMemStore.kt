package org.wit.hillfortapp.models
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

 class UserMemStore : UserStore, AnkoLogger {
    val users = ArrayList<UserModel>()

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun create(user: UserModel) {
        user.id = getId()
        users.add(user)
        logAll()
    }

    override fun update(user: UserModel) {
        val foundUser: UserModel? = users.find { u -> u.id == user.id }
        if (foundUser != null) {
            foundUser.email = user.email
            foundUser.password = user.password
        }
    }

    private fun logAll() {
        users.forEach { info("${it}") }
    }
}