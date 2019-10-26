package org.wit.hillfortapp.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.helpers.exists
import org.wit.hillfortapp.helpers.read
import org.wit.hillfortapp.helpers.write
import java.util.*
import kotlin.collections.ArrayList

const val JSON_FILE = "users.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<UserModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class UserJSONStore(val context: Context) : UserStore, AnkoLogger {

    private var users = arrayListOf<UserModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): ArrayList<UserModel> {
        return users
    }

    override fun create(user: UserModel) {
        user.id = generateRandomId().toInt()
        users.add(user)
        serialize()
    }

    override fun update(user: UserModel) {
        val foundUser: UserModel? = users.find { u -> u.id == user.id }
        if (foundUser != null) {
            foundUser.email = user.email
            foundUser.password = user.password
            foundUser.hillforts = user.hillforts
        }
        serialize()
    }

    override fun findOne(email: String, password: String): UserModel? {
        return users.singleOrNull { user ->
            user.email == email && user.password == password
        }
    }

    override fun deleteUser(user: UserModel) {
        users.remove(user)
        serialize()
    }

    // Hillfort Functionality

    override fun findAllHillforts(): ArrayList<HillfortModel> {
        val totalHillforts: ArrayList<HillfortModel> = arrayListOf() // returning 1?
        for (user in users){
            if(user.hillforts.size !=0) {
                for (hillfort in user.hillforts) {
                    totalHillforts.add(hillfort)
                }
            }
        }
        return totalHillforts
    }

    override fun findAllUserHillforts(activeUser: UserModel): ArrayList<HillfortModel> {
        return activeUser.hillforts
    }

    override fun findOneUserHillfort(hillfortID: Int, activeUser: UserModel): HillfortModel? {
        return activeUser.hillforts.singleOrNull { hillfort ->
            hillfort.id == hillfortID
        }
    }

    override fun createHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        // hillfort.id = activeUser.hillforts.size+1
        hillfort.id = generateRandomId().toInt()
        activeUser.hillforts.add(hillfort)
        serialize()
    }

    override fun updateHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        val index = activeUser.hillforts.indexOf(foundHillfort)
        if (foundHillfort != null) {
            activeUser.hillforts[index] = hillfort
        }
        serialize()
    }

    override fun deleteHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        activeUser.hillforts.remove(foundHillfort)
        serialize()
    }

    override fun deleteAllHillforts(activeUser: UserModel) {
        activeUser.hillforts.clear()
        serialize()
    }

    // Note Functionality

    override fun findOneUserHillfortNotes(activeUser: UserModel, hillfort: HillfortModel): ArrayList<Note>? {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        return foundHillfort?.notes
    }

    override fun findAllHillfortNotes(): ArrayList<Note> {
        val totalNotes = ArrayList<Note>()
        val totalHillforts = findAllHillforts()
        for(hillfort in totalHillforts) {
            totalNotes.addAll(hillfort.notes)
        }
        return totalNotes
    }

    override fun createNote(activeUser: UserModel, hillfort: HillfortModel, note: Note) {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        val index = activeUser.hillforts.indexOf(foundHillfort)
        note.id = generateRandomId().toInt()
        activeUser.hillforts[index].notes.add(note)
        serialize()
    }

    override fun updateNote(activeUser: UserModel, hillfort: HillfortModel, note: Note) {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        val foundHillfortNotes = findOneUserHillfortNotes(activeUser, hillfort)
        val foundNote = foundHillfortNotes?.singleOrNull { matchingNote ->
            matchingNote.id == note.id
        }
        val hillfortIndex = activeUser.hillforts.indexOf(foundHillfort)
        val noteIndex = activeUser.hillforts[hillfortIndex].notes.indexOf(foundNote)

        if (foundNote != null) {
            activeUser.hillforts[hillfortIndex].notes[noteIndex] = note
            serialize()
        }
    }

    override fun deleteNote(activeUser: UserModel, hillfort: HillfortModel, note: Note) {
        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
        val foundHillfortNotes = findOneUserHillfortNotes(activeUser, hillfort)
        val foundNote = foundHillfortNotes?.singleOrNull { matchingNote ->
            matchingNote.id == note.id
        }
        val hillfortIndex = activeUser.hillforts.indexOf(foundHillfort)
        val noteIndex = activeUser.hillforts[hillfortIndex].notes.indexOf(foundNote)

        if (foundNote != null) {
            activeUser.hillforts[hillfortIndex].notes.removeAt(noteIndex)
            serialize()
        }
    }

    // write methods
    private fun serialize() {
        val jsonString = gsonBuilder.toJson(users, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        users = Gson().fromJson(jsonString, listType)
    }
}