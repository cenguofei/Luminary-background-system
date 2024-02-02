package com.example.util

inline val empty: String get() = ""

inline val noSessionMsg: String get() = "You currently do not have a session."

inline val unknownErrorMsg: String get() = "Unknown error."

inline val internalErrorMsg: String get() = "Internal Error."

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

inline val id: String get() = "id"

inline val deleteSuccess: String get() = "Delete successful."


// Routing
//Article
inline val articlesRootPath: String get() = "/articles"
inline val createArticlePath: String get() = "/create"
inline val getArticleByIdPath: String get() = "/{id}"
inline val updateArticleByIdPath: String get() = "/update"
inline val deleteArticleByIdPath: String get() = "/{id}"
inline val pagesArticlePath: String get() = "/pages"
//Like
inline val likeRootPath: String get() = "/likes"
inline val createLikePath: String get() = "/create"
inline val getAllLikesOfUserPath: String get() = "/user/{id}"
inline val getAllLikesOfArticlePath: String get() = "/article/{id}"
inline val deleteLikePath: String get() = "/{id}"
//Collect
inline val collectRootPath: String get() = "/collects"
inline val createCollectPath: String get() = "/create"
inline val getAllCollectsOfUserPath: String get() = "/user/{id}"
inline val getAllCollectsOfArticlePath: String get() = "/article/{id}"
inline val deleteCollectPath: String get() = "/{id}"
//Comment
inline val commentRootPath: String get() = "/comments"
inline val createCommentPath: String get() = "/create"
inline val getAllCommentsOfUserPath: String get() = "/user/{id}"
inline val getAllCommentsOfArticlePath: String get() = "/article/{id}"
inline val deleteCommentPath: String get() = "/{id}"
