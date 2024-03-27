package com.example.dao.user

import com.example.models.User
import com.example.models.tables.Users
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.selectAll

class SearchUserDao(
    private val searchContent: String
) : DefaultUserDao() {
    override suspend fun pages(pageStart: Int, perPageCount: Int): List<User> {
        return dbTransaction {
            Users.selectAll().where { (Users.username like "%$searchContent%") }
                .limit(n = perPageCount, offset = pageOffset(pageStart, perPageCount))
                .mapToUser()
        }
    }

    override suspend fun count(): Long {
        return dbTransaction {
            Users.selectAll().where { (Users.username like "%$searchContent%") }
                .count()
        }
    }
}