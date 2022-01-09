package com.example.tasktrack.database

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "tracking_table")

data class Track(
     @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    var image: Bitmap? = null,
    var timestamp: Long = 0L,
    var averageSpeedInKm: Float = 0f,
    var distanceInmeters: Int = 0,
    var timeInMilliSeconds: Long = 0L,

    ) : Parcelable  {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readLong(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(image, flags)
        parcel.writeLong(timestamp)
        parcel.writeFloat(averageSpeedInKm)
        parcel.writeInt(distanceInmeters)
        parcel.writeLong(timeInMilliSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }


}
