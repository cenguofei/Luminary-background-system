package com.example.models.tables

import com.example.models.tables.Comments.references
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Collects : Table() {
    val id = long("id").autoIncrement()

    val collectUserId = long("collectUserId").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val articleId = long("article_id").references(
        ref = Articles.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}