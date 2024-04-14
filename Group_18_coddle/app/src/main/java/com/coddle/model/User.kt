package com.coddle.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class User(
    var userId: String = UUID.randomUUID().toString(),
    var username: String = "",
    var petName: String = "",
    var breed: String = "",
    var dob: String = "",
    var type: String = "",
    var gender: String = "",
    var email: String = "",
    var petImage:String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(petName)
        parcel.writeString(breed)
        parcel.writeString(dob)
        parcel.writeString(type)
        parcel.writeString(gender)
        parcel.writeString(email)
        parcel.writeString(petImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}