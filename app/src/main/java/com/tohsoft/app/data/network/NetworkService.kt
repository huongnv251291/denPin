package com.tohsoft.app.data.network

import com.tohsoft.app.data.models.MoreApps
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

/**
 * Created by Phong on 11/9/2016.
 */
private val service: NetworkService by lazy {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl("http://linkapp.tohapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    retrofit.create(NetworkService::class.java)
}

fun getNetworkService() = service

interface NetworkService {
    @GET("moreapp.php")
    fun moreApps(@QueryMap params: Map<String, String>): Single<MoreApps>
}