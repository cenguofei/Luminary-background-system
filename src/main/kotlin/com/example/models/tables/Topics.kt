package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object Topics : Table() {
    val id = long("id").autoIncrement()

    val topic = text("topic")

    val fashion = integer("fashion")

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}