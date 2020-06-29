package com.study.rxjavastudy.retrofitservices

import com.study.rxjavastudy.util.RequestApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/* This class is responsible for creating the Retrofit instance
and getting a reference to the RequestApi class that I defined above.

The thing to pay attention to here is the addCallAdapterFactory method call.
 Without that, we can't convert Retrofit Call objects Flowables/Observables.
*/
object ServiceGenerator{
    const val BASE_URL = "https://jsonplaceholder.typicode.com"

    private val retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//to get observable Data to RxJava
        .addConverterFactory(GsonConverterFactory.create()) //Gson convert Factory

    private val retrofit : Retrofit = retrofitBuilder.build()

    private val requestApi : RequestApi = retrofit.create(RequestApi :: class.java)

    fun getRequestApi() : RequestApi {
        return requestApi
    }
}