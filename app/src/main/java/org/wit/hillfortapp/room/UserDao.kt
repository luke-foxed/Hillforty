package org.wit.hillfortapp.room


import androidx.room.*
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.models.UserModel

@Dao
interface UserDao {

    // USER QUERIES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(user: UserModel)

    @Query("SELECT * FROM UserModel")
    fun findAllUsers(): List<UserModel>

    @Query("select * from UserModel where username = :username AND password = :password")
    fun findOneUser(username: String, password: String): UserModel

    @Query("select * from UserModel where username = :username")
    fun findUsername(username: String): UserModel?

    @Update
    fun updateUser(user: UserModel)

    @Delete
    fun deleteUser(user: UserModel)

    // HILLFORT QUERIES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createHillfort(hillfort: HillfortModel)

    @Query("SELECT * FROM HillfortModel")
    fun findAllHillforts(): List<HillfortModel>

    @Query("SELECT * FROM HillfortModel WHERE userID=:userID")
    fun findAllUserHillforts(userID: Int): List<HillfortModel>

    @Query("select * from HillfortModel where ID = :id")
    fun findHillfortByID(id: Int): HillfortModel

    @Query("select * from HillfortModel where ID = :hillfortID AND userID =:userID")
    fun findOneUserHillfort(hillfortID: Int, userID: Int): HillfortModel

    @Update
    fun updateHillfort(hillfort: HillfortModel)

    @Delete
    fun deleteHillfort(hillfort: HillfortModel)

    @Query("DELETE FROM HillfortModel WHERE userID=:userID")
    fun deleteAllHillforts(userID:Int)

    /// NOTES
    @Query("SELECT * FROM NoteModel WHERE userID=:activeUserID AND hillfortID=:hillfortID")
    fun findOneUserHillfortNotes(
        activeUserID: Int,
        hillfortID: Int
    ): List<NoteModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createUserNote(note:NoteModel)

//    override fun createNote(activeUser: UserModel, hillfort: HillfortModel, note: Note) {
//        val foundHillfort = findOneUserHillfort(hillfort.id, activeUser)
//        val index = activeUser.hillforts.indexOf(foundHillfort)
//        note.id = generateRandomId().toInt()
//        activeUser.hillforts[index].notes.add(note)
}