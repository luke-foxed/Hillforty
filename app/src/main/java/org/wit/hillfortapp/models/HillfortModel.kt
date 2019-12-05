package org.wit.hillfortapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class HillfortModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userID: Int = 0,
    var name: String = "",
    var description: String = "",
    var location: Location = Location(),
    var images: List<String> = ArrayList(),
    var notes: List<NoteModel> = ArrayList(),
    var visited: Boolean = false,
    var dateVisited: String = ""

) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

@Parcelize
@Entity
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userID: Int = 0,
    var hillfortID: Int = 0,
    var title: String = "",
    var content: String = ""
) : Parcelable