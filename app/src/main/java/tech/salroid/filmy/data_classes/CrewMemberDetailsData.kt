package tech.salroid.filmy.data_classes

data class CrewMemberDetailsData(
    var crewMemberId: String,
    var crewMemberName: String? = null,
    var crewMemberJob: String? = null,
    var crewMemberProfile: String? = null
)