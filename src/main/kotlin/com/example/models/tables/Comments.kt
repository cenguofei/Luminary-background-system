package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Comments : Table() {
    val id = long("id").autoIncrement()

    /**
     * 评论的用户已注销，则评论也应该删除
     */
    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val content = mediumText("content")

    /**
     * 文章被删除，用户的评论可以暂时保留
     */
    val articleId = long("article_id").references(
        ref = Articles.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}