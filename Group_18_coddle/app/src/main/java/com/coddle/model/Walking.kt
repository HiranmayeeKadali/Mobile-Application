package com.coddle.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Walking(
    val id: String = UUID.randomUUID().toString(),
    var distance: String = "0",
    var steps: String = "0",
    var date: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(distance)
        parcel.writeString(steps)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Walking> {
        override fun createFromParcel(parcel: Parcel): Walking {
            return Walking(parcel)
        }

        override fun newArray(size: Int): Array<Walking?> {
            return arrayOfNulls(size)
        }
    }
}
