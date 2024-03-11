package com.example.plugins.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.models.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

object JwtConfig {
    private const val secretKey = "Fls78FNBo013nFU72foQP07fFI91dbIOwe45eF8gOpn894PJls49JvqDb6537Bre"
    private const val issuer = "luminary.blog"
//    private const val validityInMillis = 36_000_00 * 12 // 1小时
//    private const val freshValidityInMillis = 36_000_00 * 12 * 2 // 1小时

    private const val validityInMillis = 100 * 6000000000000000 * 1 // 1小时
    private const val freshValidityInMillis = 200 * 6000000000000000 * 1 // 1小时
    private val algorithm = Algorithm.HMAC512(secretKey)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

    /**
     * 为用户生成 AccessToken
     */
    fun generateAccessToken(user: User): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.id)
            .withClaim("username", user.username)
            .withExpiresAt(expiration())
            .sign(algorithm)

    /**
     * 为用户生成RefreshToken
     */
    fun generateRefreshToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withClaim("username", user.username)
        .withExpiresAt(expiration(false))
        .sign(algorithm)

    /**
     * 计算失效日期
     */
    private fun expiration(isAccess: Boolean = true): Date =
        Date(System.currentTimeMillis() + if (isAccess) validityInMillis else freshValidityInMillis)
}

/**
 * 登录后再次发送请求可获取到user
 */
val ApplicationCall.jwtUser get() = authentication.principal<User>()
