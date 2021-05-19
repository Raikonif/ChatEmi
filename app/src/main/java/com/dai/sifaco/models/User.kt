package com.dai.sifaco.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val rank: String, val ocupation: String,val verify: String, val fulled: String): Parcelable {
    constructor() : this("", "", "","","", "", "")
}