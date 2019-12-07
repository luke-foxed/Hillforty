package org.wit.hillfortapp.models.rooms

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.ImageModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.NoteModel

class Convertor {

    @TypeConverter
    fun restoreList(listOfHillforts: String?): List<HillfortModel?>? {
        return Gson().fromJson<List<HillfortModel?>>(
            listOfHillforts,
            object : TypeToken<List<HillfortModel?>?>() {}.type
        )
    }

    @TypeConverter
    fun saveListHillfort(listOfHillforts: List<HillfortModel?>?): String? {
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
    fun saveImages(images: List<ImageModel>?): String? {
        return Gson().toJson(images)
    }

    @TypeConverter
    fun restoreImages(images: String?): List<ImageModel>? {
        val type = object : TypeToken<List<ImageModel>>() {}.type
        return Gson().fromJson(images, type)
    }

    @TypeConverter
    fun saveNotes(notes: List<NoteModel>?): String? {
        val type = object : TypeToken<List<NoteModel>>() {}.type
        return Gson().toJson(notes, type)
    }

    @TypeConverter
    fun restoreNotes(notes: String?): List<NoteModel>? {
        return Gson().fromJson(
            notes,
            object : TypeToken<List<NoteModel>?>() {}.type
        )
    }
}