package com.codingwithrufat.userapplication.network

import com.codingwithrufat.userapplication.network.models.UserResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET("users?")
    fun getAllUsers(
        @Query("page") page: Int,
        @Query("order") order: String,
        @Query("inname") name: String,
        @Query("site") site: String
    ): Observable<UserResponse>

}