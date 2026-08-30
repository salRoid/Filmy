package tech.salroid.filmy.ui.common.model

data class WatchProvidersUiModel(
    val link: String?,
    val providers: List<ProviderUiModel>
)

data class ProviderUiModel(
    val id: Int,
    val name: String,
    val logoPath: String
)
