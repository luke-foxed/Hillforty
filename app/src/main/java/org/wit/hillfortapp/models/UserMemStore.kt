package org.wit.hillfortapp.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.*


var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class UserMemStore : UserStore, AnkoLogger {
    val users = ArrayList<UserModel>()
    var gson = Gson()

    override fun findAll(): List<UserModel> {
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

    fun export(context: Context) {
        try {

            TODO("NOT WORKING, FILE NOT BEING WRITTEN")

            val data = gson.toJson(users)
            val path = context.filesDir.parentFile

            val tempFile: File = File.createTempFile("users", ".json", path)
            val output = BufferedWriter(FileWriter(tempFile))
            output.write(data)
            output.close()

            // gson.toJson(users, FileWriter(path))

        } catch (e: IOException) {
            Log.e("Error", "", e)
        }
    }
}