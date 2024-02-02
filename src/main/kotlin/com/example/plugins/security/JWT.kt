package com.example.plugins.security

import com.example.dao.user.UserDao
import com.example.dao.user.UserDaoFacadeImpl
import com.example.models.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun Application.configureJWT() {
    /**
     * 认证，认证用户身份. [原理](https://blog.csdn.net/Tdh5258/article/details/119064695?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522170642500716800213026508%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=170642500716800213026508&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-2-119064695-null-null.142^v99^control&utm_term=bearer&spm=1018.2226.3001.4187)
     * jwt基于bearer
     *   install(Authentication) {
     *       bearer(name = "auth-bearer") {
     *           // realm 属性设置要在 WWW-Authenticate 标头中传递的领域
     *           realm =  "Access to the '/' path"
     *           authenticate { bearerTokenCredential ->
     *                if (bearerTokenCredential.token == "luminary1122") {
     *                   UserIdPrincipal("chenguofei")
     *               } else {
     *                   null
     *               }
     *           }
     *       }
     *   }
     */
    val userDao: UserDao = UserDaoFacadeImpl()

    install(Authentication) {
        /**
         * Set up the JWT authentication to be used in [Routing].
         * If the token is valid, the corresponding [User] is fetched from the database.
         * The [User] can then be accessed in each [ApplicationCall].
         */
        jwt {
            verifier(JwtConfig.verifier)
            realm = "luminary.blog"
            validate {
                it.payload.getClaim("id").asLong()?.let { id ->
                    userDao.read(id)
                }
            }
        }
    }
}