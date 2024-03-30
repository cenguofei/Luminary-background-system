package com.example.routings.user.status

import com.example.routings.user.logout
import com.example.util.logd
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * 1. �û���¼[com.example.routings.user.login]ͬ���޸�ǰ̨״̬������Ҫ�پ������ϱ��ӿ�
 *      isForeground = true
 *
 * 2. �û�����ǰ̨&��¼״̬����ʱ�ϱ�
 *      isForeground = true
 *
 * 2. �û����ں�̨&��¼״̬����ʱ�ϱ�
 *      isForeground = false
 *
 * 2. �û��˳���¼ʱ[logout]ͬ���޸�ǰ̨״̬������Ҫ�پ������ϱ��ӿ�
 *      isForeground = false
 */
@kotlinx.serialization.Serializable
data class Status(
    /**
     * �ͻ����Ƿ���ǰ̨
     */
    val isForeground: Boolean = false,

    val username: String
)

object StatusManager {
    private val allUserStatus = ConcurrentHashMap<String, Status>()

    fun addOrUpdateStatus(status: Status) {
        val copy = status.copy()
        allUserStatus[copy.username] = copy
        "��������״̬�û�������û�����״��, status=$status".logd()
    }

    fun removeStatus(username: String) {
        if (allUserStatus.containsKey(username)) {
            allUserStatus.remove(username)
        }
    }

    /**
     * ǰ̨�û�
     */
    fun onlineUsers() : List<String> {
        return allUserStatus.filter { it.value.isForeground }
            .map { it.value.username }
    }

    /**
     * ��¼״̬���ڵ��ǲ���ǰ̨
     */
    fun offlineUsers() : List<String> {
        return allUserStatus.filter { !it.value.isForeground }
            .map { it.value.username }
    }
}