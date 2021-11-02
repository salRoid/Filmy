package tech.salroid.filmy.data

data class CastMemberDetailsData @JvmOverloads constructor(
    var castId: String,
    var castName: String? = null,
    var castRolePlayed: String? = null,
    var castDisplayProfile: String? = null
)