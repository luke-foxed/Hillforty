package org.wit.hillfortapp.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class HillfortModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var fbId: String = "",
    var name: String = "",
    var description: String = "",
    var visited: Boolean = false,
    var dateVisited: String = "",
    var rating: Int = 0,
    @Embedded var location: Location = Location(),
    @Embedded var images: ArrayList<ImageModel> = ArrayList(),
    @Embedded var notes: ArrayList<NoteModel> = ArrayList()

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
    var fbId: String = "",
    var title: String = "",
    var content: String = ""
) : Parcelable


@Parcelize
@Entity
data class ImageModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var fbID: String = "",
    var uri: String = ""
): Parcelable
