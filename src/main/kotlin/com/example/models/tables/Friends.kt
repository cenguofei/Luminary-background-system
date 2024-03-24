package com.example.models.tables

import com.example.models.tables.Friends.userId
import com.example.models.tables.Friends.whoId
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/**
 * Friends define:
 * Mutual attention
 * 当[userId]和[whoId]都为null时，就可以直接清除
 */
object Friends : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id")
        .references(
            ref = Users.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        ).nullable()

    /**
     * 当我关注的人注销账户：
     * 我关注的用户，当该用户注销时，把whoId设置为null，
     * 给用户一个提示，该账户已经注销，并显示注销头像、用户名
     */
    val whoId = long("who_id")
        .references(
            ref = Users.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        ).nullable()

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}

const val DELETED_USER_ID = -998L

const val DEFAULT_FRIENDS_PAGE_COUNT = 24
