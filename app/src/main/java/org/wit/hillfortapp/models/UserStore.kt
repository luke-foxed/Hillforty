package org.wit.hillfortapp.models

interface UserStore {

    // User functionality
    fun findAll(): ArrayList<UserModel>
    fun findUsername(username: String): Boolean
    fun findOne(username: String, password: String): UserModel?
    fun create(user: UserModel)
    fun update(user: UserModel)
    fun deleteUser(user: UserModel)

    // Hillfort functionality
    fun findAllHillforts(): ArrayList<HillfortModel>?
    fun findAllUserHillforts(activeUser: UserModel): ArrayList<HillfortModel>
    fun findOneHillfort(hillfortID: Int): HillfortModel?
    fun findOneUserHillfort(hillfortID: Int, activeUser: UserModel): HillfortModel?
    fun createHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun updateHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun deleteHillfort(hillfort: HillfortModel, activeUser: UserModel)
    fun deleteAllHillforts(activeUser: UserModel)

    // Note functionality
    fun findOneUserHillfortNotes(activeUser: UserModel, hillfort: HillfortModel): ArrayList<Note>?
    fun findAllHillfortNotes(): ArrayList<Note>
    fun createNote(activeUser: UserModel, hillfort: HillfortModel, note: Note)
    fun updateNote(activeUser: UserModel, hillfort: HillfortModel, note: Note)
    fun deleteNote(activeUser: UserModel, hillfort: HillfortModel, note: Note)

}