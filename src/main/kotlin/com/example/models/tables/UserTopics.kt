package com.example.models.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

object UserTopics : Table() {
    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val topics = json<Array<Long>>("topics", Json.Default)

    override val primaryKey = PrimaryKey(userId)
}