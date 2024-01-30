package com.example.util

inline val empty: String get() = ""

inline val noSessionMsg: String get() = "You currently do not have a session."

inline val unknownErrorMsg: String get() = "Unknown error."

inline val fileUrl: String get() = "file_url"

inline val resRoot: String get() = "res/"

inline val serverError: String get() = "Server error occurred."

inline val cachePath: String get() = "build/ehcache"

inline val usersLongCacheAlias: String get() = "usersLongCache"

inline val usersStringCacheAlias: String get() = "usersStringCache"

inline val Long.Companion.Default: Long get() = 0L

inline val Int.Companion.Default: Int get() = 0

inline val invalidId: String get() = "Invalid id."

inline val noArticle: String get() = "There is no such article"


// Routing
inline val articlesRootPath: String get() = "/articles"
inline val createArticlePath: String get() = "/create"
inline val getArticleByIdPath: String get() = "/{id}"
inline val updateArticleByIdPath: String get() = "/{id}"
inline val deleteArticleByIdPath: String get() = "/{id}"