package com.example.dao.user

import com.example.models.Role
import com.example.models.Sex
import com.example.models.User
import com.example.models.UserStatus
import com.example.models.tables.Users
import com.example.plugins.database.database
import com.example.util.dbTransaction
import com.example.util.encrypt
import com.example.util.logd
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserDaoImpl : UserDao {

    init {
        transaction(database) { SchemaUtils.create(Users) }
    }

    override suspend fun create(data: User): Long = dbTransaction {
        Users.insert {
            it[username] = data.username
            it[age] = data.age
            it[sex] = data.sex.toString()
            it[headUrl] = data.headUrl
            it[background] = data.background
            it[password] = encrypt(data.password)
            it[role] = data.role.toString()
            it[status] = data.status.toString()
            it[birth] = data.birth
            it[signature] = data.signature
            it[location] = data.location
            it[blogAddress] = data.blogAddress
        }[Users.id]
    }

    override suspend fun read(id: Long): User? = read { Users.id eq id }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? =
        read { Users.username eq username }

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

    override suspend fun pageCount(): Long {
        return count()
    }

    override suspend fun existing(id: Long): Boolean {
        return dbTransaction {
            Users.selectAll().where {
                Users.id eq id
            }.limit(1).firstOrNull() != null
        }
    }

    private suspend fun update(selector: () -> Op<Boolean>, user: User) {
        dbTransaction {
            Users.update(
                where = { selector() },
                body = {
                    it[username] = user.username
                    it[age] = user.age
                    it[sex] = user.sex.toString()
                    it[headUrl] = user.headUrl
                    it[background] = user.background
                    if (user.password.isNotBlank()) {
                        it[password] = encrypt(user.password)
                    }
                    it[role] = user.role.toString()
                    it[status] = user.status.toString()

                    it[birth] = user.birth
                    it[signature] = user.signature
                    it[location] = user.location
                    it[blogAddress] = user.blogAddress
                }
            )
        }
    }

    private suspend fun read(
        selector: () -> Op<Boolean>
    ): User? = dbTransaction {
        Users.selectAll().where { selector() }
            .limit(1)
            .mapToUser()
            .singleOrNull()
    }
}

fun Iterable<ResultRow>.mapToUser(): List<User> {
    return map { it.mapRowToUser() }
}

fun ResultRow.mapRowToUser(): User {
    val row = this
    return User(
        username = row[Users.username],
        age = row[Users.age],
        sex = Sex.valueOf(row[Users.sex]),
        id = row[Users.id],
        headUrl = row[Users.headUrl],
        background = row[Users.background],
        role = Role.valueOf(row[Users.role]),
        status = UserStatus.valueOf(row[Users.status]),
        password = row[Users.password],
        birth = row[Users.birth],
        signature = row[Users.signature],
        location = row[Users.location],
        blogAddress = row[Users.blogAddress]
    )
}