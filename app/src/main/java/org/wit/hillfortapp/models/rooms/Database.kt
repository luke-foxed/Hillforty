package org.wit.hillfortapp.models.rooms


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.models.UserModel

@Database(entities = [UserModel::class, HillfortModel::class, NoteModel::class], version = 1, exportSchema = false)
@TypeConverters(Convertor::class)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
}