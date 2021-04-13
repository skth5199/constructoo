package com.ald.uniofsouthampton.constructoo.recycler

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VendorMaterialModel(val materialID : String,
                               val name       :String,
                               val weight     :String,
                               val dimensions :String,
                               val colour     :String) : Parcelable
