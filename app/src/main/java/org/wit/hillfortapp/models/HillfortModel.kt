package org.wit.hillfortapp.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class HillfortModel(
    var id: Int = 0,
    var userID: Int = 0,
    var name: String = "",
    var description: String = "",
    var location: Location = Location(),
    var images: ArrayList<String> = ArrayList(),
    var notes: ArrayList<Note> = ArrayList(),
    var visited: Boolean = false,
    var dateVisited: String = ""

) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

@Parcelize
data class Note(
    var id: Int = 0,
    var title: String = "",
    var content: String = ""
) : Parcelable