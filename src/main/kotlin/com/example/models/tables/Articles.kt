package com.example.models.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

object Articles : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val username = varchar("username", 50)
        .references(
            ref = Users.username,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE
        )
    val author = varchar("author", 20)
    val title = text("title")
    val link = text("link")
    val body = largeText("body")

    val niceDate = datetime("nice_date")

    val visibleMode = varchar("visible_mode", 6)

    val tags = json<Array<String>>("tags", Json.Default)

    val likes = integer("likes")
    val collections = integer("collections")
    val comments = integer("comments")

    val viewsNum = integer("views_num")

    override val primaryKey = PrimaryKey(id)
}

const val DELETED_ARTICLE_ID = -999L