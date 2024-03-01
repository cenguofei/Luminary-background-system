package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = long("id").autoIncrement()
    val username = varchar("username", length = 50)
        .uniqueIndex()
    val age = integer("age")
    val sex = varchar("sex", length = 6)
    val headUrl = text("head_url")
    val password = varchar("password", length = 50)
    val role = varchar("role", length = 8)

    val status = varchar("status", length = 8)

    override val primaryKey = PrimaryKey(id)
}

const val DEFAULT_USERS_PAGE_COUNT = 24




/*
where:

eq - (==)
neq - (!=)
isNull()
isNotNull()
less - (<)
lessEq - (<=)
greater - (>)
greaterEq - (>=)
like - (=~)
notLike - (!~)
exists
notExists
regexp
inList
notInList
between
match (MySQL MATCH AGAINST)
isDistinctFrom (null-safe equality comparison)
isNotDistinctFrom (null-safe equality comparison)


Allowed logical conditions are:

not
and
or
andIfNotNull
orIfNotNull
compoundAnd()
compoundOr()
 */
