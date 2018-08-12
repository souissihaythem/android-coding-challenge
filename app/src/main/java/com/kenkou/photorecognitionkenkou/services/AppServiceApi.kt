package com.kenkou.photorecognitionkenkou.services

import com.kenkou.photorecognitionkenkou.BuildConfig
import com.kenkou.photorecognitionkenkou.models.RequestImage
import com.kenkou.photorecognitionkenkou.models.ResponsesImage
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

interface AppServiceApi {

    @POST("v1/images:annotate")
    fun getImage(@HeaderMap headers: Map<String, String>,
                 @Query("key") key: String,
                 @Body requests: RequestImage): Observable<ResponsesImage>


    companion object {
        fun create(): AppServiceApi = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .build()
                .create(AppServiceApi::class.java)
    }

}