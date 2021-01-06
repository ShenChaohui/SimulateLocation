package com.sch.simulatelocation.network

import android.text.TextUtils
import android.util.Log
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.sch.simulatelocation.MyApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Sch.
 * Date: 2020/12/11
 * description:
 */
object NetWorkManager {
    //实现cookies 持久化的一种简便方式
    var cookieJar: ClearableCookieJar = PersistentCookieJar(
        SetCookieCache(),
        SharedPrefsCookiePersistor(MyApplication.getContext())
    )
    private val okHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.i("okHttpLog", it)
        })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val interceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(interceptor)
            .cookieJar(cookieJar)
            .connectTimeout(120L, TimeUnit.SECONDS)
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(120L, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://restapi.amap.com/v3/")
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().enableComplexMapKeySerialization().create()
                )
            )
            .build()
    }
    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

}