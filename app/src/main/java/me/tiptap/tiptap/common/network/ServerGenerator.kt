package me.tiptap.tiptap.common.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServerGenerator {

    companion object {

        private const val BASE_URL = "http://13.209.117.190:8080/" //temp


        private fun getClient(): OkHttpClient =
                OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .build()

        private fun getInstance(): Retrofit =
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()


        //retrofit 구현체에 인터페이스 정보를 보냄.
        fun <T> createService(serviceClass: Class<T>): T = getInstance().create(serviceClass)
    }

}