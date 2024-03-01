package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/**
 * ��[userId]��[whoId]��Ϊnullʱ���Ϳ���ֱ�����
 */
object Friends : Table() {
    val id = long("id").autoIncrement()

    val userId = long("user_id")
        .references(
            ref = Users.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        ).nullable()

    /**
     * ���ҹ�ע����ע���˻���
     * �ҹ�ע���û��������û�ע��ʱ����whoId����Ϊnull��
     * ���û�һ����ʾ�����˻��Ѿ�ע��������ʾע��ͷ���û���
     */
    val whoId = long("who_id")
        .references(
            ref = Users.id,
            onDelete = ReferenceOption.SET_NULL,
            onUpdate = ReferenceOption.CASCADE
        ).nullable()

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}

const val DELETED_USER_ID = -998L

const val DEFAULT_FRIENDS_PAGE_COUNT = 24
