package org.wit.hillfortapp.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
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
    var hillforts: List<HillfortModel> = ArrayList()
) : Parcelable