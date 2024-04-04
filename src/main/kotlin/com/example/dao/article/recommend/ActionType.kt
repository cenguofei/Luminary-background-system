package com.example.dao.article.recommend

/**
 * �û���Ϊ����ö��
 * @property weight Ȩ��
 */
enum class ActionType(val weight: Int) {
    /**
     * ���������������
     */
    VIEW(1),

    /**
     * ���ѵ��޹�������
     */
    LIKE(2),

    /**
     * �������۹�������
     */
    Comment(3),

    /**
     * �����ղص�����
     */
    COLLECT(4)
}
