package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object ViewDurations : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val type = text("type")

    val duration = long("duration")

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}
