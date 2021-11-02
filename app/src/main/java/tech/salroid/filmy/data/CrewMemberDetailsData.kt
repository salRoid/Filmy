package tech.salroid.filmy.data

data class CrewMemberDetailsData @JvmOverloads constructor(
    var crewMemberId: String,
    var crewMemberName: String? = null,
    var crewMemberJob: String? = null,
    var crewMemberProfile: String? = null
)