package com.example.dao.friend

import com.example.models.*
import com.example.models.ext.UserFriend
import com.example.models.tables.DELETED_USER_ID
import com.example.models.tables.Friends
import com.example.models.tables.Users
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import com.example.util.isNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

class FriendDaoImpl : FriendDao {
    init { transaction(database) { SchemaUtils.create(Friends) } }

    override suspend fun create(data: Friend): Long = dbTransaction {
        Friends.insert {
            it[userId] = data.userId
            it[whoId] = data.whoId
            val ts = if (data.timestamp == Long.Default) System.currentTimeMillis() else data.timestamp
            it[timestamp] = ts
        }[Friends.id]
    }

    override suspend fun delete(id: Long) {
        dbTransaction {
            Friends.deleteWhere { Friends.id eq id }
        }
    }

    override suspend fun delete(userId: Long, whoId: Long) {
        dbTransaction {
            Friends.deleteWhere { Friends.userId eq userId and Friends.whoId.eq(whoId) }
        }
    }

    override suspend fun read(id: Long): Friend? = read { Friends.id eq id }

    override suspend fun myFollowings(id: Long): List<Friend> = dbTransaction {
        Friends.selectAll().where { Friends.userId eq id }
            .distinct()
            .mapToFriend()
    }

    override suspend fun allFollowMeOnlyFriends(id: Long): List<Friend> = dbTransaction {
        Friends.selectAll().where { Friends.whoId eq id }
            .distinct()
            .mapToFriend()
    }

    override suspend fun allFollowMeToUsers(loginUserId: Long): List<UserFriend> {
        return dbTransaction {
            Users.innerJoin(
                otherTable = Friends,
                onColumn = { this.id },
                otherColumn = { this.userId },
                additionalConstraint = {
                    Friends.whoId eq loginUserId
                }
            ).selectAll().mapToUserFriend()
        }
    }

    override suspend fun deleteBothNull() {
        dbTransaction {
            Friends.deleteWhere { whoId.isNull() and userId.isNull() }
        }
    }

    override suspend fun unfollow(userId: Long, who: Long) {
        dbTransaction {
            Friends.deleteWhere {
                Friends.userId eq userId and whoId.eq(who)
            }
        }
    }

    override suspend fun existingOfUserId(userId: Long): Boolean {
        return !read { Friends.userId eq userId }.isNull
    }

    override suspend fun existingOfWhoId(whoId: Long): Boolean {
        return !read { Friends.whoId eq whoId }.isNull
    }

    override suspend fun existing(userId: Long, whoId: Long): Boolean {
        return !read { Friends.userId eq userId and Friends.whoId.eq(whoId) }.isNull
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Friend> =
        Friends.getPageQuery(pageStart, perPageCount).mapToFriend()

    private fun Iterable<ResultRow>.mapToFriend() : List<Friend> {
        return map {
            Friend(
                id = it[Friends.id],
                userId = it[Friends.userId] ?: DELETED_USER_ID,
                whoId = it[Friends.whoId] ?: DELETED_USER_ID,
                timestamp = it[Friends.timestamp]
            )
        }
    }

    private suspend fun read(selector: () -> Op<Boolean>) : Friend? = dbTransaction {
        Friends.selectAll().where { selector() }.limit(1).map {
            Friend(
                id = it[Friends.id],
                userId = it[Friends.userId] ?: DELETED_USER_ID,
                whoId = it[Friends.whoId] ?: DELETED_USER_ID,
                timestamp = it[Friends.timestamp]
            )
        }.singleOrNull()
    }

    private fun Iterable<ResultRow>.mapToUserFriend(): List<UserFriend> {
        return map {
            UserFriend(
                user = User(
                    username = it[Users.username],
                    age = it[Users.age],
                    sex = Sex.valueOf(it[Users.sex]),
                    id = it[Users.id],
                    headUrl = it[Users.headUrl],
                    background = it[Users.background],
                    role = Role.valueOf(it[Users.role]),
                    status = UserStatus.valueOf(it[Users.status]),
                    password = it[Users.password]
                ),
                beFriendTime = it[Friends.timestamp]
            )
        }
    }
}