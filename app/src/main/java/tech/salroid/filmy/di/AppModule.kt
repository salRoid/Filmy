package tech.salroid.filmy.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.MIGRATION_2_4
import tech.salroid.filmy.data.local.db.MIGRATION_4_5
import tech.salroid.filmy.data.network.*
import tech.salroid.filmy.utility.PreferenceHelper
import tech.salroid.filmy.utility.resolveApiLanguageTag
import tech.salroid.filmy.utility.resolveImageLanguages

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideOkhttpClient(appPref: SharedPreferences): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val original = it.request()
            val originalUrl = original.url
            val url = originalUrl.newBuilder()
                //.addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            val requestBuilder = it.request().newBuilder().url(url)
                .addHeader("content-type", "application/json")
            // Calls made on the user's behalf (v4 lists) carry the user's own
            // access token; everything else is authorised as the app.
            if (original.header("Authorization") == null) {
                requestBuilder.addHeader("authorization", "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}")
            }
            val request = requestBuilder.build()
            it.proceed(request)

        }.addInterceptor {
            val originalUrl = it.request().url.toUrl().path
            var v4Url: String = it.request().url.toUrl().toString()
            if (originalUrl.contains("/auth/")) {
                v4Url = v4Url.replace("/3/", "/4/")
            }
            it.proceed(it.request().newBuilder().url(v4Url).build())

        }.addInterceptor { chain ->
            val original = chain.request()
            val request = if (original.url.host == "api.themoviedb.org") {
                val country = appPref.getString(PreferenceHelper.COUNTRY_KEY, null) ?: "US"
                val language = resolveApiLanguageTag(AppCompatDelegate.getApplicationLocales())
                original.newBuilder()
                    .url(
                        original.url.newBuilder()
                            .setQueryParameter("region", country)
                            .setQueryParameter("language", language)
                            .apply {
                                if (original.url.encodedPath.endsWith("/images")) {
                                    setQueryParameter(
                                        "include_image_language",
                                        resolveImageLanguages(language)
                                    )
                                }
                            }
                            .build()
                    )
                    .build()
            } else {
                original
            }
            chain.proceed(request)

        }.addInterceptor(HttpLoggingInterceptor {
            Log.d("OkHttp", it)
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
            // Requests can now carry the user's own access token.
            redactHeader("Authorization")
        }).build()

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(MoviesApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    fun provideMoviesApiService(retrofit: Retrofit): MoviesApiService {
        return retrofit.create(MoviesApiService::class.java)
    }

    @Provides
    fun provideMoviesApiHelper(apiService: MoviesApiService): MoviesApiHelper {
        return MoviesApiHelperImpl(apiService)
    }

    @Provides
    fun provideAccountApiService(retrofit: Retrofit): AccountApiService {
        return retrofit.create(AccountApiService::class.java)
    }

    @Provides
    fun provideAccountApiHelper(apiService: AccountApiService): AccountApiHelper {
        return AccountApiHelperImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideMoviesDatabase(@ApplicationContext appContext: Context): FilmyDatabase {
        return Room.databaseBuilder(
            appContext,
            FilmyDatabase::class.java,
            "filmy"
        )
            .addMigrations(MIGRATION_2_4, MIGRATION_4_5)
            .build()
    }

    @Provides
    fun provideAppPreference(@ApplicationContext appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }
}