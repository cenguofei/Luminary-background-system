package com.example.models.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Comments : Table() {
    val id = long("id").autoIncrement()

    /**
     * ���۵��û���ע����������ҲӦ��ɾ��
     */
    val userId = long("user_id").references(
        ref = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val content = mediumText("content")

    /**
     * ���±�ɾ�����û������ۿ�����ʱ����
     */
    val articleId = long("article_id").references(
        ref = Articles.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}