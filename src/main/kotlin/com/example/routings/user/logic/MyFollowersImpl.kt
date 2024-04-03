package com.example.routings.user.logic

import com.example.dao.user.mapToUser
import com.example.models.User
import com.example.models.tables.Friends
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class MyFollowersImpl : MyFollowers {
    override suspend fun myFollowers(loginUserId: Long): List<User> {
        return dbTransaction {
            Friends.innerJoin(
                otherTable = Users,
                onColumn = {
                    this.userId
                },
                otherColumn = {
                    this.id
                },
                additionalConstraint = {
                    Friends.whoId eq loginUserId
                }
            ).selectAll().mapToUser()
        }
    }
}