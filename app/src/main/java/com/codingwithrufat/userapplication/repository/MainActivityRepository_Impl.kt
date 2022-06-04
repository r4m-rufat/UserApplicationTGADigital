package com.codingwithrufat.userapplication.repository

import com.codingwithrufat.userapplication.network.models.UserResponse
import io.reactivex.Observable

interface MainActivityRepository_Impl {
    fun getUsers(page: Int, order: String, name: String, site: String): Observable<UserResponse>
}