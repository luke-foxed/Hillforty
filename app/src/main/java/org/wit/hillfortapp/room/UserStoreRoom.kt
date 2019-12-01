package org.wit.hillfortapp.room

import android.content.Context
import androidx.room.Room
import org.wit.hillfortapp.models.*

class UserStoreRoom(val context: Context) : UserStore {

    var dao: UserDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.userDao()
    }

    override fun findAll(): List<UserModel> {
        return dao.findAllUsers()
    }

    override fun findUsername(username: String): Boolean {
        return dao.findUsername(username) != null
    }

    override fun findOne(username: String, password: String): UserModel? {
        return dao.findOneUser(username, password)
    }

    override fun create(user: UserModel) {
        dao.create(user)
    }

    override fun update(user: UserModel) {
        dao.updateUser(user)
    }

    override fun deleteUser(user: UserModel) {
        dao.deleteUser(user)
    }

    /*
    -------
    Hillfort Functionality
    -------
    */

    override fun findAllHillforts(): List<HillfortModel>? {
        return dao.findAllHillforts()
    }

    override fun findAllUserHillforts(activeUser: UserModel): List<HillfortModel> {
        return dao.findAllUserHillforts(activeUser.id)
    }

    override fun findOneHillfort(hillfortID: Int): HillfortModel? {
        return dao.findHillfortByID(hillfortID)
    }

    override fun findOneUserHillfort(hillfortID: Int, activeUser: UserModel): HillfortModel? {
        return dao.findOneUserHillfort(hillfortID, activeUser.id)
    }

    override fun createHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        return dao.createHillfort(hillfort)
    }

    override fun updateHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        return dao.updateHillfort(hillfort)
    }

    override fun deleteHillfort(hillfort: HillfortModel, activeUser: UserModel) {
        return dao.deleteHillfort(hillfort)
    }

    override fun deleteAllHillforts(activeUser: UserModel) {
        return dao.deleteAllHillforts(activeUser.id)
    }



    ////



    override fun findOneUserHillfortNotes(
        activeUserID: Int,
        hillfortID: Int
    ): List<NoteModel>? {
return dao.findOneUserHillfortNotes(activeUserID, hillfortID)   }

    override fun findAllHillfortNotes(): List<NoteModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createNote(note: NoteModel) {
        dao.createUserNote(note)
    }

    override fun updateNote(activeUser: UserModel, hillfort: HillfortModel, note: NoteModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteNote(activeUser: UserModel, hillfort: HillfortModel, note: NoteModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}