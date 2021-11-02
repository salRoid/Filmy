package tech.salroid.filmy.data_classes

data class CastMemberDetailsData(
    var castId: String,
    var castName: String? = null,
    var castRolePlayed: String? = null,
    var castDisplayProfile: String? = null
)