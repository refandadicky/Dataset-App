package com.example.datasetapp.data.source.network.service

import android.util.Base64
import com.example.datasetapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        fun invoke(): DatasetApiService {
            val okHttpClient =
                OkHttpClient.Builder()
                    .addInterceptor(BasicAuthInterceptor("DATASET_APP", "yu6W742s9BcEAvFm3VQCNgZUzjfeqhRDpSrGPYdM8"))
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()
            val retrofit =
                Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            return retrofit.create(DatasetApiService::class.java)
        }
    }

    class BasicAuthInterceptor(username: String, password: String) : Interceptor {
        private val credentials: String = "Basic " + Base64.encodeToString(
            "$username:$password".toByteArray(), Base64.NO_WRAP
        )

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", credentials)
                .build()
            return chain.proceed(request)
        }
    }
}