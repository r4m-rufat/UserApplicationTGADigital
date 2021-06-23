package com.kivitool.roomdatabasetutorial.datas.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class User (

    @PrimaryKey(autoGenerate = true)
    val userID: Int? = null,
    val username: String? = null,
    val surname: String? = null,
    val gmail: String? = null

    )