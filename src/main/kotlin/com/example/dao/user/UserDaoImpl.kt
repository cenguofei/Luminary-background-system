package com.example.dao.user

import com.example.util.dbTransaction
import com.example.models.Role
import com.example.models.Sex
import com.example.models.User
import com.example.models.UserStatus
import com.example.models.tables.Articles
import com.example.models.tables.Users
import com.example.plugins.database.database
import com.example.util.empty
import com.example.util.encrypt
import com.example.util.logd
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserDaoImpl : UserDao {

    init { transaction(database) { SchemaUtils.create(Users) } }

    override suspend fun create(data: User): Long = dbTransaction {
        Users.insert {
            it[username] = data.username
            it[age] = data.age
            it[sex] = data.sex.toString()
            it[headUrl] = data.headUrl
            it[password] = encrypt(data.password)
            it[role] = data.role.toString()
            it[status] = data.status.toString()
        }[Users.id]
    }

    override suspend fun read(id: Long): User? = read { Users.id eq id }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? =
        read(pwdNeeded = pwdNeeded) { Users.username eq username }

    override suspend fun update(id: Long, data: User) =
        update(selector = { Users.id eq id }, data)

    override suspend fun updateViaRead(id: Long, update: (old: User) -> User) {
        dbTransaction {
            val readUser = read(id)
            "readUser=$readUser, update=${readUser?.let { update(it) }}".logd("delete_user")
            readUser?.let {
                update(id, update(it))
            }
        }
    }

    override suspend fun updateByUsername(username: String, user: User) =
        update(selector = { Users.username eq username }, user)

    override suspend fun delete(id: Long) {
        dbTransaction {
            Users.deleteWhere { Users.id eq id }
        }
    }

    override suspend fun batchUsers(ids: List<Long>): List<User> {
        return dbTransaction {
            Users.selectAll().where { Users.id inList ids }
                .mapToUser()
        }
    }

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User> =
        Users.getPageQuery(pageStart, perPageCount).mapToUser()

    private suspend fun update(selector: () -> Op<Boolean>, user: User) {
        dbTransaction {
            Users.update(
                where = { selector() },
                body = {
                    it[username] = user.username
                    it[age] = user.age
                    it[sex] = user.sex.toString()
                    it[headUrl] = user.headUrl
                    if (user.password.isNotEmpty()) {
                        it[password] = encrypt(user.password)
                    }
                    it[role] = user.role.toString()
                    it[status] = user.status.toString()
                }
            )
        }
    }

    private suspend fun read(
        pwdNeeded: Boolean = false,
        selector: () -> Op<Boolean>
    ): User? = dbTransaction {
        Users.selectAll().where { selector() }
            .limit(1)
            .mapToUser(pwdNeeded)
            .singleOrNull()
    }

    private fun Iterable<ResultRow>.mapToUser(pwdNeeded: Boolean = false) : List<User> {
        return map {
            User(
                username = it[Users.username],
                age = it[Users.age],
                sex = Sex.valueOf(it[Users.sex]),
                id = it[Users.id],
                headUrl = it[Users.headUrl],
                role = Role.valueOf(it[Users.role]),
                status = UserStatus.valueOf(it[Users.status]),
                password = if (pwdNeeded) it[Users.password] else empty
            )
        }
    }
}
