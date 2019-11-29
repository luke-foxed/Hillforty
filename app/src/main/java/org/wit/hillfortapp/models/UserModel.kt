package org.wit.hillfortapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var hillforts: ArrayList<HillfortModel> = ArrayList()
) : Parcelable