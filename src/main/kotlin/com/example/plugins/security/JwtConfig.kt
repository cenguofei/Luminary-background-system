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

    private const val validityInMillis = 100 * 6000000000000000 * 1L // 1小时
    private const val freshValidityInMillis = 200 * 6000000000000000 * 1 // 1小时

    /**
     * HmacSHA512是一种对称密钥算法
     *
     * HMAC 是密钥相关的 哈希运算消息认证码（Hash-based Message Authentication Code），
     * HMAC 运算利用 哈希算法 (MD5、SHA1 等)，以 一个密钥 和 一个消息 为输入，生成一个 消息摘要 作为 输出。
     * HMAC 发送方 和 接收方 都有的 key 进行计算，而没有这把 key 的第三方，
     * 则是 无法计算 出正确的 散列值的，这样就可以 防止数据被篡改。
     */
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
