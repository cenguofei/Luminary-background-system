package com.example.routings.file

@kotlinx.serialization.Serializable
data class UploadData(
    val filenames: List<String>,
) {
    fun first(): String = filenames[0]
}

const val UPLOAD_TYPE_USER_HEAD = 0
const val UPLOAD_TYPE_ARTICLE_COVER = 1
const val UPLOAD_TYPE_OTHER = 2
const val UPLOAD_TYPE_USER_BACKGROUND = 3


val uploadTypes = listOf(
    UPLOAD_TYPE_USER_HEAD,
    UPLOAD_TYPE_ARTICLE_COVER,
    UPLOAD_TYPE_OTHER,
    UPLOAD_TYPE_USER_BACKGROUND
)
