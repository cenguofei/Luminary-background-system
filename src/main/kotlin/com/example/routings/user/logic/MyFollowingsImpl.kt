package com.example.routings.user.logic

import com.example.dao.user.mapToUser
import com.example.models.User
import com.example.models.tables.Friends
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class MyFollowingsImpl : MyFollowings {
    override suspend fun myFollowings(loginUserId: Long): List<User> {
        return dbTransaction {
            Users.innerJoin(
                otherTable = Friends,
                onColumn = {
                    this.id
                },
                otherColumn = {
                    this.whoId
                },
                additionalConstraint = {
                    Friends.userId eq loginUserId
                }
            ).selectAll().mapToUser()
        }
    }
}