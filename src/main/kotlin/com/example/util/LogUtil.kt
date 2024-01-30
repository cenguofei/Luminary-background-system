package com.example.util

import org.slf4j.LoggerFactory

fun String.logi(tag: String = "cgf") {
    val logger = LoggerFactory.getLogger(tag)
    logger.info(this)
}

fun String.logw(tag: String = "cgf") {
    val logger = LoggerFactory.getLogger(tag)
    logger.warn(this)
}

fun String.loge(tag: String = "cgf") {
    val logger = LoggerFactory.getLogger(tag)
    logger.error(this)
}

fun String.logd(tag: String = "cgf") {
    val logger = LoggerFactory.getLogger(tag)
    logger.debug(this)
}

fun String.logt(tag: String = "cgf") {
    val logger = LoggerFactory.getLogger(tag)
    logger.trace(this)
}

fun String.withLogi(tag: String = "cgf") : String {
    logi(tag)
    return this
}

fun String.withLogw(tag: String = "cgf") : String {
    logw(tag)
    return this
}

fun String.withLoge(tag: String = "cgf") : String {
    loge(tag)
    return this
}

fun String.withLogd(tag: String = "cgf") : String {
    logd(tag)
    return this
}

fun String.withLogt(tag: String = "cgf") : String {
    logt(tag)
    return this
}