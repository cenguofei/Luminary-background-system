package com.example.plugins.security

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun verify(token: String): DecodedJWT? {
    val jwt = try {
        JwtConfig.verifier.verify(token)
    } catch (cause: JWTVerificationException) {
        cause.printStackTrace()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    return jwt
}

suspend fun DecodedJWT.validate(
    call: ApplicationCall,
    validate: suspend ApplicationCall.(JWTCredential) -> Principal?
): Principal? {
    val payload = parsePayload()
    val credentials = JWTCredential(payload)
    return validate(call, credentials)
}

private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.getUrlDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}