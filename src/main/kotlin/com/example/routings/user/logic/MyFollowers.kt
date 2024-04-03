package com.example.routings.user.logic

import com.example.models.User

interface MyFollowers {


    suspend fun myFollowers(
        loginUserId: Long
    ): List<User>

    companion object : MyFollowers by MyFollowersImpl()
}