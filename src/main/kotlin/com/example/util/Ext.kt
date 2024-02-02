package com.example.util

import io.ktor.server.application.*

val Any?.isNull: Boolean get() = this == null

val ApplicationCall.id: Long get() = parameters["id"]?.toLong()!!