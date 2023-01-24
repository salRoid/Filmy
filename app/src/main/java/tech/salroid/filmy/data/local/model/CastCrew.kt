package tech.salroid.filmy.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class CastCrew : Parcelable {
    @Parcelize
    data class CastData(val cast: Cast) : CastCrew()

    @Parcelize
    data class CrewData(val crew: Crew) : CastCrew()
}