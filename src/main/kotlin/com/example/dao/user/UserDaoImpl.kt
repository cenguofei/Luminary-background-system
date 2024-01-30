package com.example.dao.user

import com.example.util.dbTransaction
import com.example.models.Role
import com.example.models.Sex
import com.example.models.User
import com.example.models.Users
import com.example.util.empty
import com.example.util.encrypt
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

const val ID_OFFSET = 10000

class UserDaoImpl(
    database: Database = com.example.plugins.database.database
) : UserDao {

    init {
        transaction(database) { SchemaUtils.create(Users) }
    }

    override suspend fun create(user: User): Long = dbTransaction {
        Users.insert {
            it[username] = user.username
            it[age] = user.age
            it[sex] = user.sex.toString()
            it[headUrl] = user.headUrl
            it[password] = encrypt(user.password)
            it[role] = user.role.toString()
        }[Users.id]
    }

    override suspend fun readById(id: Long): User? = read { Users.id eq id - ID_OFFSET }

    override suspend fun readByUsername(username: String, pwdNeeded: Boolean): User? =
        read(pwdNeeded = pwdNeeded) { Users.username eq username }

    override suspend fun updateById(id: Long, user: User) =
        update(selector = { Users.id eq id - ID_OFFSET }, user)

    override suspend fun updateByUsername(username: String, user: User) =
        update(selector = { Users.username eq username }, user)

    override suspend fun delete(id: Long) {
        dbTransaction {
            Users.deleteWhere { Users.id.eq(id - ID_OFFSET) }
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
                    if (user.password.isNotEmpty()) {
                        it[password] = encrypt(user.password)
                    }
                    it[role] = user.role.toString()
                }
            )
        }
    }

    private suspend fun read(
        pwdNeeded: Boolean = false,
        selector: () -> Op<Boolean>
    ): User? = dbTransaction {
        Users.select { selector() }.map {
            User(
                username = it[Users.username],
                age = it[Users.age],
                sex = Sex.valueOf(it[Users.sex]),
                id = it[Users.id] + ID_OFFSET,
                headUrl = it[Users.headUrl],
                role = Role.valueOf(it[Users.role]),
                password = if (pwdNeeded) it[Users.password] else empty
            )
        }.singleOrNull()
    }
}
