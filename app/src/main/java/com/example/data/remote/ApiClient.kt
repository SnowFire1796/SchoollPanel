package com.example.data.remote

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {

    private val prefs = context.getSharedPreferences("school_panel_api_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_BASE_URL = "https://api.schoolpanel.ir/api/v1/"
        private const val KEY_BASE_URL = "custom_base_url"
    }

    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) {
            val formatted = if (value.endsWith("/")) value else "$value/"
            prefs.edit().putString(KEY_BASE_URL, formatted).apply()
            rebuildRetrofit(formatted)
        }

    private var retrofit: Retrofit
    var apiService: SchoolApiService
        private set

    init {
        retrofit = buildRetrofit(baseUrl)
        apiService = retrofit.create(SchoolApiService::class.java)
    }

    private fun rebuildRetrofit(url: String) {
        retrofit = buildRetrofit(url)
        apiService = retrofit.create(SchoolApiService::class.java)
    }

    private fun buildRetrofit(url: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
