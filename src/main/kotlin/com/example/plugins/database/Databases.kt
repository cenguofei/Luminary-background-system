package com.example.plugins.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig

private fun createHikariDataSource() = HikariDataSource(
    HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3306/luminary_blog"
        username = "root"
        password = "123456"
        maximumPoolSize = 12
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
)

val database = Database.connect(
    createHikariDataSource(),
    databaseConfig = DatabaseConfig().apply {

    }
)

