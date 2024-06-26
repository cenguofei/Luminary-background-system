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
//    private const val validityInMillis = 36_000_00 * 12 // 1Сʱ
//    private const val freshValidityInMillis = 36_000_00 * 12 * 2 // 1Сʱ

    private const val validityInMillis = 100 * 6000000000000000 * 1L // 1Сʱ
    private const val freshValidityInMillis = 200 * 6000000000000000 * 1 // 1Сʱ

    /**
     * HmacSHA512��һ�ֶԳ���Կ�㷨
     *
     * HMAC ����Կ��ص� ��ϣ������Ϣ��֤�루Hash-based Message Authentication Code����
     * HMAC �������� ��ϣ�㷨 (MD5��SHA1 ��)���� һ����Կ �� һ����Ϣ Ϊ���룬����һ�� ��ϢժҪ ��Ϊ �����
     * HMAC ���ͷ� �� ���շ� ���е� key ���м��㣬��û����� key �ĵ�������
     * ���� �޷����� ����ȷ�� ɢ��ֵ�ģ������Ϳ��� ��ֹ���ݱ��۸ġ�
     */
    private val algorithm = Algorithm.HMAC512(secretKey)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

    /**
     * Ϊ�û����� AccessToken
     */
    fun generateAccessToken(user: User): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.id)
            .withClaim("username", user.username)
            .withExpiresAt(expiration())
            .sign(algorithm)

    /**
     * Ϊ�û�����RefreshToken
     */
    fun generateRefreshToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withClaim("username", user.username)
        .withExpiresAt(expiration(false))
        .sign(algorithm)

    /**
     * ����ʧЧ����
     */
    private fun expiration(isAccess: Boolean = true): Date =
        Date(System.currentTimeMillis() + if (isAccess) validityInMillis else freshValidityInMillis)
}

/**
 * ��¼���ٴη�������ɻ�ȡ��user
 */
val ApplicationCall.jwtUser get() = authentication.principal<User>()
