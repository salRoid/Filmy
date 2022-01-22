package tech.salroid.filmy.data.network

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.salroid.filmy.BuildConfig

object RetrofitClient {

    var filmyApi: FilmyApi

    init {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor {
            val original: Request = it.request()
            val originalHttpUrl: HttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            // Request customization: add request headers
            val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)
            val request: Request = requestBuilder.build()
            it.proceed(request)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(FilmyApi.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        filmyApi = retrofit.create(FilmyApi::class.java)
    }
}