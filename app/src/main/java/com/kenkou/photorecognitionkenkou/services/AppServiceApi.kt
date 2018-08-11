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
import retrofit2.http.*

interface AppServiceApi {

    @Headers("Content-Type: application/json" ,
            "Authorization: Bearer ya29.c.El_2BaE8Btw1S6d9jCdWjPVsClEB-IZgBKorMyth_zdB15J2BKBrXYAAF5nCUJ7OnOmWhYp_JMo4JeWXbCJecJSe2bO7kaP00cJTSgzU_RbitJwo17gT4m0K4WiVk7O6bA")
    @POST("v1/images:annotate")
    fun getImage(@Query("key") key: String,
                 @Body requests: RequestImage): Observable<ResponsesImage>


    companion object {
        fun create(): AppServiceApi = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .build()
                .create(AppServiceApi::class.java)
    }

}