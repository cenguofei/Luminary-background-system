package com.example.routings.user.logic

import com.example.models.User

interface MyFollowings {

    suspend fun myFollowings(
        loginUserId: Long
    ): List<User>

    companion object : MyFollowings by MyFollowingsImpl()
}