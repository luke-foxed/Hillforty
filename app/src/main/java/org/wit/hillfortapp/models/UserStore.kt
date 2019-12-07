package org.wit.hillfortapp.models

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
    fun findAllUserHillforts(activeUserID: Int): List<HillfortModel>
    fun findOneHillfort(hillfortID: Int): HillfortModel?
    fun findOneUserHillfort(hillfortID: Int, activeUserID: Int): HillfortModel
    fun createHillfort(hillfort: HillfortModel)
    fun updateHillfort(hillfort: HillfortModel)
    fun deleteHillfort(hillfort: HillfortModel)
    fun deleteAllHillforts(activeUserID: Int)

    // Note functionality
    fun findOneUserHillfortNotes(activeUserID: Int, hillfortID: Int): List<NoteModel>?
    fun findAllHillfortNotes(): List<NoteModel>
    fun createNote(noteModel: NoteModel)
    fun deleteNote(noteModel: NoteModel)

    // Image functionality
    fun findOneUserHillfortImages(activeUserID: Int, hillfortID: Int): List<ImageModel>
    fun createImage(imageModel: ImageModel)

}