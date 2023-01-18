package tech.salroid.filmy.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.network.MoviesApiHelper
import tech.salroid.filmy.data.network.MoviesApiHelperImpl
import tech.salroid.filmy.data.network.MoviesApiService

@Module
@InstallIn(SingletonComponent::class)
object MoviesModule {

    @Provides
    fun provideOkhttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val original = it.request()
            val originalHttpUrl: HttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()
            it.proceed(request)
        }.build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(MoviesApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideMoviesService(retrofit: Retrofit): MoviesApiService {
        return retrofit.create(MoviesApiService::class.java)
    }

    @Provides
    fun provideMoviesApiHelper(apiService: MoviesApiService): MoviesApiHelper {
        return MoviesApiHelperImpl(apiService)
    }

    @Provides
    fun provideMoviesDatabase(@ApplicationContext appContext: Context): FilmyDatabase {
        return Room.databaseBuilder(
            appContext,
            FilmyDatabase::class.java,
            "filmy"
        ).build()
    }
}