package org.wit.hillfortapp.models

import org.w3c.dom.Node

interface UserStore {

    // User functionality
    fun findAll(): List<UserModel>
    fun findUsername(username: String): Boolean
    fun findOne(username: String, password: String): UserModel?
    fun create(user: UserModel)
    fun update(user: UserModel)
    fun deleteUser(user: UserModel)

    // Hillfort functionality
    fun findAllHillforts(): List<HillfortModel>?
    fun findAllUserHillforts(activeUser: UserModel): List<HillfortModel>
    fun findOneHillfort(hillfortID: Int): HillfortModel?
    fun findOneUserHillfort(hillfortID: Int, activeUser: UserModel): HillfortModel?
    fun createHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun updateHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun deleteHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun deleteAllHillforts(activeUser: UserModel)

    // Note functionality
    fun findOneUserHillfortNotes(activeUserID: Int, hillfortID: Int): MutableList<NoteModel>?
    fun findAllHillfortNotes(): List<NoteModel>
    fun createNote(note: NoteModel)
    fun updateNote(activeUser: UserModel, hillfort: HillfortModel, note: NoteModel)
    fun deleteNote(note:NoteModel)

}