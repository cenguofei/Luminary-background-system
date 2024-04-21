package com.example.plugins

import com.example.models.responses.DataResponse
import com.example.routings.article.collect.configureCollectRouting
import com.example.routings.article.comment.configureCommentRouting
import com.example.routings.article.configureArticleRouting
import com.example.routings.article.like.configureLikeRouting
import com.example.routings.article.message.configureMessageRouting
import com.example.routings.common.configureCommonRouting
import com.example.routings.file.configureFileRouting
import com.example.routings.search.configureSearchRouting
import com.example.routings.token.configureTokenRouting
import com.example.routings.topic.configureTopicRouting
import com.example.routings.topic.updateTopicRank
import com.example.routings.user.configureUserRouting
import com.example.routings.user.friend.configureFriendRouting
import com.example.routings.user.status.configureReportOnlineStatusRouting
import com.example.routings.wanandroid.configureWanandroidRouting
import com.example.routings.ws.configureWebSocketRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title

fun Application.configureRouting() {
    configureUserRouting()
    configureFileRouting()
    configureArticleRouting()
    configureLikeRouting()
    configureCollectRouting()
    configureCommentRouting()
    configureFriendRouting()
    configureTokenRouting()
    configureReportOnlineStatusRouting()
    configureCommonRouting()
    configureWebSocketRouting()
    configureMessageRouting()
    configureSearchRouting()
    configureWanandroidRouting()
    configureTopicRouting()

    updateTopicRank()
    routing {
        route("/") {
            get {
                call.respondHtml {
                    head {
                        title {
                            +"Lunimary Blog"
                        }
                    }
                    body {
                        h1 {
                            +"Hello, Welcome to explore Lunimary Blog."
                        }
                    }
                }
            }
            get("index") {
                call.respondHtml {
                    head {
                        title {
                            +"Lunimary Blog"
                        }
                    }
                    body {
                        h1 {
                            +"Hello, Welcome to explore Lunimary Blog."
                        }
                    }
                }
            }
        }
        route("/privacy") {
            get {
                coroutineScope {
                    delay(2000L)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = DataResponse<String>().copy(
                            data = """
                            # Lunimary Blog 隐私政策

                            欢迎使用 Lunimary Blog 应用程序（以下简称“本应用程序”）。我们非常重视您的隐私。本隐私政策旨在向您说明我们收集、使用和保护您在使用本应用程序时提供给我们的个人信息。

                            ## 收集的信息

                            我们可能会收集以下类型的个人信息：

                            - 您提供的个人资料，例如姓名、电子邮件地址和个人配置文件信息；
                            - 您在使用本应用程序时创建的博客和相关内容；
                            - 与您的设备相关的信息，例如设备类型、操作系统版本和唯一设备标识符；
                            - 使用分析和跟踪技术收集的信息，用于改善本应用程序的性能和用户体验。
                            - 朋友关系信息： 我们可能会收集您与其他用户之间的朋友关系信息，例如您与其他用户的关注关系或好友关系。
                            - 浏览行为信息： 当您使用本应用程序浏览、点赞、收藏文章时，我们可能会收集与这些行为相关的信息，例如浏览的文章ID、点赞的文章ID、收藏的文章ID等。

                            ## 信息的使用

                            我们可能会使用收集的个人信息来：

                            - 向您提供并维护本应用程序的功能和服务；
                            - 处理您的博客创建和发布请求；
                            - 向您发送与本应用程序相关的通知和更新；
                            - 改善本应用程序的功能和性能；
                            - 提供客户支持和解决技术问题；
                            - 进行统计分析和研究，以改进本应用程序的用户体验。
                            - 偏好分析： 基于上述收集的信息，我们可能会进行偏好分析，以推算您的兴趣和偏好，并为您提供相关的推荐文章列表。



                            ## 信息的保护

                            我们采取安全措施来保护您的个人信息免遭未经授权的访问、使用或披露。然而，您应该意识到，互联网上的数据传输并非绝对安全，我们不能保证通过互联网传输的信息的绝对安全性。

                            ## 信息的共享

                            除非获得您的明确同意或法律要求，否则我们不会向第三方披露您的个人信息。

                            ## 变更和修订

                            我们可能会不时更新本隐私政策。更新的隐私政策将在本应用程序上发布，并取代先前的隐私政策。请您定期查阅本隐私政策以了解最新变更。

                            ## 联系我们

                            如果您对本隐私政策或关于您个人信息的问题有任何疑问，请通过以下联系方式与我们联系：

                            - Email: cenguofei@gmail.com

                            感谢您阅读我们的隐私政策。希望您继续享受Lunimary Blog应用程序的优质服务。
                        """.trimIndent()
                        )
                    )
                }
            }
        }
    }
}

suspend inline fun <reified T : Any> ApplicationCall.receive(
    processError: Boolean = true,
    errorMessage: Any? = null,
    onError: (CannotTransformContentToTypeException) -> Unit = {},
    onSuccess: (T) -> Unit
) {
    val data = receiveNullable<T>(typeInfo<T>())
    if (data != null) {
        onSuccess(data)
    } else {
        val exception = CannotTransformContentToTypeException(typeInfo<T>().kotlinType!!)
        if (processError) {
            respond(
                status = HttpStatusCode.InternalServerError,
                message = errorMessage ?: DataResponse<Unit>().copy(
                    msg = HttpStatusCode.InternalServerError.description
                )
            )
        } else {
            onError(exception)
        }
    }
}