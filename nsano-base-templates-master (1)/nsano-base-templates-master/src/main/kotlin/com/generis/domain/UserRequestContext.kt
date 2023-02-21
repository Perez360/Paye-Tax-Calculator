package com.generis.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.lang3.StringUtils


object UserRequestContext {
    private const val DEFAULT_LANGUAGE = "en"
    private val currentLanguage: ThreadLocal<String?> = InheritableThreadLocal()
    private val currentClientVersion: ThreadLocal<String?> = InheritableThreadLocal()
    private val currentOS: ThreadLocal<String?> = InheritableThreadLocal()
    private val currentModelNumber: ThreadLocal<String?> = InheritableThreadLocal()

    fun getCurrentLanguage(): String? {
        return currentLanguage.get()
    }

    fun setCurrentLanguage(language: String?) {
        if (StringUtils.isNotBlank(language))
            currentLanguage.set(language)
        else currentLanguage.set(DEFAULT_LANGUAGE)

    }

    fun getCurrentClientVersion(): String? {
        return currentClientVersion.get()
    }

    fun setCurrentClientVersion(language: String?) {
        currentClientVersion.set(language)
    }

    fun getCurrentModelNumber(): String? {
        return currentModelNumber.get()
    }

    fun setCurrentModelNumber(language: String?) {
        currentModelNumber.set(language)
    }

    fun getCurrentOS(): String? {
        return currentOS.get()
    }

    fun setCurrentOS(language: String?) {
        currentOS.set(language)
    }

    override fun toString(): String {
        return jacksonObjectMapper().writeValueAsString(this)
    }


    fun clear() {
        currentLanguage.set(null)
        currentOS.set(null)
        currentClientVersion.set(null)
        currentModelNumber.set(null)
    }
}