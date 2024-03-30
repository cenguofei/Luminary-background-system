package com.example.routings.user.status

import com.example.routings.user.logout
import com.example.util.logd
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * 1. 用户登录[com.example.routings.user.login]同步修改前台状态，不需要再经过此上报接口
 *      isForeground = true
 *
 * 2. 用户处于前台&登录状态还在时上报
 *      isForeground = true
 *
 * 2. 用户处于后台&登录状态还在时上报
 *      isForeground = false
 *
 * 2. 用户退出登录时[logout]同步修改前台状态，不需要再经过此上报接口
 *      isForeground = false
 */
@kotlinx.serialization.Serializable
data class Status(
    /**
     * 客户端是否处于前台
     */
    val isForeground: Boolean = false,

    val username: String
)

object StatusManager {
    private val allUserStatus = ConcurrentHashMap<String, Status>()

    fun addOrUpdateStatus(status: Status) {
        val copy = status.copy()
        allUserStatus[copy.username] = copy
        "增加在线状态用户或更新用户在线状况, status=$status".logd()
    }

    fun removeStatus(username: String) {
        if (allUserStatus.containsKey(username)) {
            allUserStatus.remove(username)
        }
    }

    /**
     * 前台用户
     */
    fun onlineUsers() : List<String> {
        return allUserStatus.filter { it.value.isForeground }
            .map { it.value.username }
    }

    /**
     * 登录状态还在但是不在前台
     */
    fun offlineUsers() : List<String> {
        return allUserStatus.filter { !it.value.isForeground }
            .map { it.value.username }
    }
}