package org.wit.hillfortapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HillfortModel(
    var id: Long = 0,
    var name: String = "",
    var description: String = "",
    var location: String = "",
    var images: ArrayList<String> = ArrayList(),
    var notes: String= "",
    var visited: Boolean = false,
    var dateVisited: String = ""

) : Parcelable