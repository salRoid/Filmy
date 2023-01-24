package tech.salroid.filmy.data.local.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrailerData(
    val title: String?,
    val url: String?
) : Parcelable