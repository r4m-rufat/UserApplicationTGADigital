package com.kivitool.roomdatabasetutorial.datas.db

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table ORDER BY userID ASC")
    fun getAllUserInfo(): List<User>?

    @Insert
    fun insertUser(user: User?)

    @Delete
    fun deleteUser(user: User?)

    @Update
    fun updateUser(user: User?)


}