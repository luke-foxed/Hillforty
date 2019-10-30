package org.wit.hillfortapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var id: Int = 0,
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var hillforts: ArrayList<HillfortModel> = ArrayList()
) : Parcelable