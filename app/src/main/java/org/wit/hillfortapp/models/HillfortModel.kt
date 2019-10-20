package org.wit.hillfortapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HillfortModel(
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var location: Location = Location(),
    var images: ArrayList<String> = ArrayList(),
    var notes: String= "",
    var visited: Boolean = false,
    var dateVisited: String = ""

) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable