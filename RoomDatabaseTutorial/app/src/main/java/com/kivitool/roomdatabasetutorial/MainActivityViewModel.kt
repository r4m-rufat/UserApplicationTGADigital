package com.kivitool.roomdatabasetutorial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kivitool.roomdatabasetutorial.datas.db.User
import com.kivitool.roomdatabasetutorial.datas.db.UserRoomDatabase

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    lateinit var users: MutableLiveData<List<User>>

    init {
        users = MutableLiveData()
    }

    fun getAllUsersObservers(): MutableLiveData<List<User>>{

        return users

    }

    fun getAllUsersFromDatabase(){

        val userDao = UserRoomDatabase.getRoomDatabaseInfo(getApplication()).userDao()
        val list = userDao?.getAllUserInfo()

        users.postValue(list)

    }

    fun insertUserInfo(entity: User){

        val userDao = UserRoomDatabase.getRoomDatabaseInfo(getApplication()).userDao()
        userDao?.insertUser(entity)

        getAllUsersFromDatabase()


    }

    fun updateUserInfo(entity: User){

        val userDao = UserRoomDatabase.getRoomDatabaseInfo(getApplication()).userDao()
        userDao?.updateUser(entity)

        getAllUsersFromDatabase()


    }

    fun deleteUserInfo(entity: User){

        val userDao = UserRoomDatabase.getRoomDatabaseInfo(getApplication()).userDao()
        userDao?.deleteUser(entity)

        getAllUsersFromDatabase()


    }


}