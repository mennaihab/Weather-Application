package com.example.weatherapplication.models

import android.os.Parcel
import android.os.Parcelable
import androidx.navigation.NavType

data class MapLocation(
    val latitude:Double,
    val longitude:Double
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapLocation> {
        override fun createFromParcel(parcel: Parcel): MapLocation {
            return MapLocation(parcel)
        }

        override fun newArray(size: Int): Array<MapLocation?> {
            return arrayOfNulls(size)
        }
    }
}
