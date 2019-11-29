package org.wit.hillfortapp.room

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.Note

class UserConvertor {

    @TypeConverter
    fun restoreList(listOfHillforts: String?): ArrayList<HillfortModel?>? {
        return Gson().fromJson<ArrayList<HillfortModel?>>(
            listOfHillforts,
            object : TypeToken<ArrayList<HillfortModel?>?>() {}.type
        )
    }

    @TypeConverter
    fun saveListHillfort(listOfHillforts: ArrayList<HillfortModel?>?): String? {
        return Gson().toJson(listOfHillforts)
    }

    @TypeConverter
    fun restoreLocation(location: String?): Location? {
        return Gson().fromJson(
            location,
            object : TypeToken<Location?>() {}.type
        )
    }

    @TypeConverter
    fun saveLocation(location: Location?): String? {
        return Gson().toJson(location)
    }


    @TypeConverter
    fun saveImages(images: ArrayList<String>?): String? {
        return Gson().toJson(images)
    }

    @TypeConverter
    fun restoreImages(images: String?): ArrayList<String>? {
        return Gson().fromJson(
            images,
            object : TypeToken<ArrayList<String>?>() {}.type
        )
    }

    @TypeConverter
    fun saveNotes(notes: List<Note>?): String? {
        return Gson().toJson(notes)
    }

    @TypeConverter
    fun restoreNotes(notes: String?): List<Note>? {
        return Gson().fromJson(
            notes,
            object : TypeToken<List<Note>?>() {}.type
        )
    }
}