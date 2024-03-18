package com.example.util

inline val empty: String get() = ""

inline val noSessionMsg: String get() = "You currently do not have a session."

inline val unknownErrorMsg: String get() = "Unknown error."

inline val internalErrorMsg: String get() = "Internal Error."

inline val fileUrl: String get() = "file_url"

inline val resRoot: String get() = "res"

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

//Users
inline val userRootPath: String get() = "/users"
inline val getUserPath: String get() = "/{id}"
inline val updateUserPath: String get() = "/{id}"
inline val deleteUserPath: String get() = "/{id}"
inline val checkIsLoginPath: String get() = "/isLogin"
inline val loginPath: String get() = "/login"
inline val logoutPath: String get() = "/logout"
inline val registerPath: String get() = "/register"
// Routing
//Page
inline val pageRootPath: String get() = "/pages"
inline val pageArticlesPath: String get() = pageRootPath
inline val pageUsersPath: String get() = pageRootPath
inline val pageCollectsPath: String get() = pageRootPath
inline val pageLikesPath: String get() = pageRootPath
inline val pageCommentsPath: String get() = pageRootPath
//Article
inline val articlesRootPath: String get() = "/articles"
inline val createArticlePath: String get() = "/create"
inline val getArticleByIdPath: String get() = "/{id}"
inline val updateArticleByIdPath: String get() = "/update"
inline val deleteArticleByIdPath: String get() = "/{id}"
inline val likesOfUserPath: String get() = "/likes_num/{userId}"
inline val articlesOfUserPath: String get() = "/user/{userId}"
inline val privacyArticlesOfUserPath: String get() = "/user/privacy/{userId}"
inline val publicArticlesOfUserPath: String get() = "/user/public/{userId}"
inline val getAllArticlesOfUserCollectedPath: String get() = "/user_collected/{userId}"
inline val getAllArticlesOfUserLikedPath: String get() = "/user_liked/{userId}"

//Like
inline val likeRootPath: String get() = "/likes"
inline val createLikePath: String get() = "/create"
inline val getAllLikesOfUserPath: String get() = "/user/{id}"
inline val getAllLikesOfArticlePath: String get() = "/article/{id}"
inline val deleteLikePath: String get() = "/{id}"
inline val cancelLikePath: String get() = "/cancel"
inline val existsLikePath: String get() = "/exists"
//Collect
inline val collectRootPath: String get() = "/collects"
inline val createCollectPath: String get() = "/create"
inline val getAllCollectsOfArticlePath: String get() = "/article/{id}"
inline val deleteCollectPath: String get() = "/{id}"
inline val existsCollectPath: String get() = "/exists"
inline val cancelCollectPath: String get() = "/cancel"

//Comment
inline val commentRootPath: String get() = "/comments"
inline val createCommentPath: String get() = "/create"
inline val getAllCommentsOfUserPath: String get() = "/user/{id}"
inline val getAllCommentsOfArticlePath: String get() = "/article/{articleId}"
inline val deleteCommentPath: String get() = "/{id}"
//Friend
inline val friendRootPath: String get() = "/friends"
inline val followPath: String get() = "/follow"
inline val unfollowPath: String get() = "/unfollow/{whoId}"
inline val myFollowingsPath: String get() = "/following/{userId}"
inline val myFollowersPath: String get() = "/followers/{userId}"
inline val existingFriendshipPath: String get() = "/existing_friendship"
//Token
inline val refreshToken: String get() = "/token/refresh"
//Online Status
inline val isForegroundStr: String get() = "isForeground"
inline val onlineStatusPath: String get() = "/online_status/{$isForegroundStr}"

//WebSocket
inline val wsRootPath: String get() = "/ws"
inline val wsCommentPath: String get() = "$wsRootPath/comment"
inline val wsLikePath: String get() = "$wsRootPath/like"
inline val wsFollowPath: String get() = "$wsRootPath/follow"

//Message
inline val messageRootPath: String get() = "/message"
inline val messageCommentPath: String get() = "/comment"
inline val messageLikePath: String get() = "/like"
inline val messageFollowPath: String get() = "/follow"