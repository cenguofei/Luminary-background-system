package com.example.models

import com.example.util.Default
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * �ҵĹ�ע��
 * select * from friends
 *      where myId == Friends.userId
 *
 * ��ע�ҵģ�
 * select * from friends
 *      where myId == Friends.whoId
 */
@kotlinx.serialization.Serializable
data class Friend(
    val id: Long = Long.Default,

    /**
     * �ҵ�id
     */
    val userId: Long = Long.Default,

    /**
     * �ҹ�ע��˭
     */
    val whoId: Long = Long.Default,

    /**
     * ʲôʱ���ע��
     */
    val timestamp: Long = Long.Default
) : java.io.Serializable {
    /**
     * ��Ϊ���Ѷ�����
     */
    val howLong: String get() = LocalDateTime.parse(
        (System.currentTimeMillis() - timestamp).toString(), formatterToDay
    ).toLocalDate().toString()
}


val formatterToDay: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")