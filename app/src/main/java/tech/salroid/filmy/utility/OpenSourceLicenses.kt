package tech.salroid.filmy.utility

data class OpenSourceLicense(
    val name: String,
    val license: String,
    val url: String
)

val OPEN_SOURCE_LICENSES = listOf(
    OpenSourceLicense(
        name = "Kotlin",
        license = "Apache License 2.0",
        url = "https://github.com/JetBrains/kotlin/blob/master/LICENSE"
    ),
    OpenSourceLicense(
        name = "Kotlin Coroutines",
        license = "Apache License 2.0",
        url = "https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "kotlinx.serialization",
        license = "Apache License 2.0",
        url = "https://github.com/Kotlin/kotlinx.serialization/blob/master/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "AndroidX Jetpack (Compose, Room, Paging, Navigation, Lifecycle, Glance)",
        license = "Apache License 2.0",
        url = "https://developer.android.com/jetpack/androidx/licenses"
    ),
    OpenSourceLicense(
        name = "Material Components for Android",
        license = "Apache License 2.0",
        url = "https://github.com/material-components/material-components-android/blob/master/LICENSE"
    ),
    OpenSourceLicense(
        name = "Dagger Hilt",
        license = "Apache License 2.0",
        url = "https://github.com/google/dagger/blob/master/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "Retrofit",
        license = "Apache License 2.0",
        url = "https://github.com/square/retrofit/blob/master/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "OkHttp",
        license = "Apache License 2.0",
        url = "https://github.com/square/okhttp/blob/master/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "Coil",
        license = "Apache License 2.0",
        url = "https://github.com/coil-kt/coil/blob/main/LICENSE.txt"
    ),
    OpenSourceLicense(
        name = "Firebase (Analytics, Crashlytics)",
        license = "Apache License 2.0",
        url = "https://github.com/firebase/firebase-android-sdk/blob/master/LICENSE"
    )
)
