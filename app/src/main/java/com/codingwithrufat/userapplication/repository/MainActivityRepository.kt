package com.codingwithrufat.userapplication.repository

import com.codingwithrufat.userapplication.network.Retrofit
import com.codingwithrufat.userapplication.network.models.UserResponse
import io.reactivex.Observable

class MainActivityRepository: MainActivityRepository_Impl {

    override fun getUsers(page: Int, order: String, name: String, site: String): Observable<UserResponse> = Retrofit.create().getAllUsers(page, order, name, site)

}