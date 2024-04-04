package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ViewArticles : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val articleId = long("article_id").references(
        ref = Articles.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}