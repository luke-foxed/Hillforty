package org.wit.hillfortapp.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.*


var lastId = 0

internal fun getId(): Int {
    return lastId++
}

class UserMemStore : UserStore, AnkoLogger {
    val users = ArrayList<UserModel>()
    var gson = Gson()

    override fun findAll(): ArrayList<UserModel> {
        return users
    }

    override fun findOne(email: String, password: String): UserModel {
        return users.single { user ->
            user.email == email && user.password == password
        }
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