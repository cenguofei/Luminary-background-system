package com.example.routings.user.logic

import com.example.util.empty

/**
 * У���û����������Ƿ����Ҫ��
 * @return [Pair] first: �Ƿ�ͨ����second: Ϊʲô��ͨ��
 */
fun verifyInput(
    username: String,
    password: String
): Pair<Boolean, String> {
    if (username.isBlank() || password.isBlank()) {
        return false to "Username or password cannot be empty."
    }

    if (!isUsernameValid(username)) {
        return false to "$username is not a valid username. " +
                "The username cannot contain special characters."
    }

    if (!isPasswordValid(password)) {
        return false to "$password is not a valid password. " +
                "Passwords can only be a combination of numbers, letters, and special symbols"
    }

    if (password.length <= PASSWORD_LENGTH) {
        return false to "The password length cannot be less than $PASSWORD_LENGTH."
    }

    return true to empty
}

const val PASSWORD_LENGTH = 6

/**
 * ֻ�ܰ�����ĸ�����֡����ֻ��������֣����ܰ���+-=/�ȷ���
 */
fun isUsernameValid(username: String): Boolean {
    val pattern = Regex("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    return pattern.matches(username)
}

/**
 * ����ֻ�ܰ�����ĸ�����ֻ�����Ӣ�ķ���
 */
fun isPasswordValid(password: String): Boolean {
    val pattern = Regex("^[a-zA-Z0-9+\\-*/@!#%&_=.,?;:$~`]+$")
    return pattern.matches(password)
}