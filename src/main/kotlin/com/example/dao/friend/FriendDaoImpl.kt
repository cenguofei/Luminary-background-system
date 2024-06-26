package com.example.dao.friend

import com.example.dao.user.mapRowToUser
import com.example.models.Friend
import com.example.models.ext.UserFriend
import com.example.models.tables.DELETED_USER_ID
import com.example.models.tables.Friends
import com.example.models.tables.Users
import com.example.plugins.database.database
import com.example.util.Default
import com.example.util.dbTransaction
import com.example.util.isNull
import com.example.util.logw
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

class FriendDaoImpl : FriendDao {
    init {
        transaction(database) { SchemaUtils.create(Friends) }
    }

    override suspend fun create(data: Friend): Long = dbTransaction {
        Friends.insert {
            it[userId] = data.userId
            it[whoId] = data.whoId
            val ts = if (data.timestamp == Long.Default) System.currentTimeMillis() else data.timestamp
            it[timestamp] = ts
            it[visibleToOwner] = data.visibleToOwner
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

    override suspend fun userFollow(ids: List<Long>): List<Friend> {
        return dbTransaction {
            Friends.selectAll().where {
                Friends.userId inList ids
            }.mapToFriend()
        }
    }

    override suspend fun myFollowings(loginUserId: Long): List<Friend> = dbTransaction {
        Friends.selectAll().where { Friends.userId eq loginUserId }
            .mapToFriend()
    }

    override suspend fun allFollowMeOnlyFriends(loginUserId: Long): List<Friend> = dbTransaction {
        Friends.selectAll().where { Friends.whoId eq loginUserId }
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
            ).selectAll().mapToUserFriend().sortedByDescending { it.beFriendTime }
        }
    }

    override suspend fun mutualFollowUsers(loginUserId: Long): List<UserFriend> {
        return dbTransaction {
            val followMe = FriendDao.allFollowMeOnlyFriends(loginUserId).map { it.userId }
            "followMe=$followMe".logw("follow_me")
            Users.innerJoin(
                otherTable = Friends,
                onColumn = {
                    this.id
                },
                otherColumn = {
                    this.whoId
                },
                additionalConstraint = {
                    //我关注了他
                    (Friends.userId eq loginUserId) and
                            (Friends.whoId inList followMe) //他也要关注我
                }
            ).selectAll()
                .mapToUserFriend()
                .sortedByDescending { it.beFriendTime }
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

    override suspend fun pageCount(): Long {
        return count()
    }

    override suspend fun read(userId: Long, whoId: Long): Friend? {
        return read { Friends.userId eq userId and Friends.whoId.eq(whoId) }
    }

    override suspend fun update(data: Friend) {
        dbTransaction {
            Friends.update(where = { Friends.id eq data.id }) { state ->
                state[visibleToOwner] = data.visibleToOwner
            }
        }
    }

    private fun Iterable<ResultRow>.mapToFriend(): List<Friend> {
        return map {
            Friend(
                id = it[Friends.id],
                userId = it[Friends.userId] ?: DELETED_USER_ID,
                whoId = it[Friends.whoId] ?: DELETED_USER_ID,
                timestamp = it[Friends.timestamp],
                visibleToOwner = it[Friends.visibleToOwner]
            )
        }
    }

    private suspend fun read(selector: () -> Op<Boolean>): Friend? = dbTransaction {
        Friends.selectAll().where { selector() }.limit(1).map {
            Friend(
                id = it[Friends.id],
                userId = it[Friends.userId] ?: DELETED_USER_ID,
                whoId = it[Friends.whoId] ?: DELETED_USER_ID,
                timestamp = it[Friends.timestamp],
                visibleToOwner = it[Friends.visibleToOwner]
            )
        }.singleOrNull()
    }

    private fun Iterable<ResultRow>.mapToUserFriend(): List<UserFriend> {
        return map {
            UserFriend(
                user = it.mapRowToUser(),
                beFriendTime = it[Friends.timestamp],
                visibleToOwner = it[Friends.visibleToOwner]
            )
        }
    }
}