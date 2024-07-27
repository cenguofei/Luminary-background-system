package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Audits : Table() {
    val id = long("id").autoIncrement()

    val auditorId = long("auditor_id")
        .references(
            ref = Users.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        )

    val articleId = long("article_id")
        .references(
            ref = Articles.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        )

    val prevState = varchar("prev_state", 10)

    val toState = varchar("to_state", 10)

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}