package com.example.myshop.models

import android.os.Parcel
import android.os.Parcelable



class User(
        // this is data class
        val uid: String? =  "",
        val firstName: String? =  "",
        val lastName: String? =  "",
        val uemail: String? =  "",
        val image: String? =  "",
         val mobile:Long =  0,
         val gender: String? =  "",
        val profileCompleted:Int =  0 ,
       // profileCompleted

):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(uemail)
        parcel.writeString(image)
        parcel.writeLong(mobile)
        parcel.writeString(gender)
        parcel.writeInt(profileCompleted)
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