package com.example.plugins.database

import com.example.models.User
import com.example.util.cachePath
import com.example.util.usersLongCacheAlias
import com.example.util.usersStringCacheAlias
import org.ehcache.Cache
import org.ehcache.PersistentCacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

val storagePath: String = cachePath

val cacheManager: PersistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
    .with(CacheManagerPersistenceConfiguration(File(storagePath)))
    .withUserCache(Long::class.javaObjectType, usersLongCacheAlias)
    .withUserCache(String::class.javaObjectType, usersStringCacheAlias)
    .build(true)

private fun <K> CacheManagerBuilder<PersistentCacheManager>.withUserCache(
    keyType: Class<K>,
    alias: String
): CacheManagerBuilder<PersistentCacheManager> {
    val builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(
        keyType, // keyType
        User::class.java, // valueType
        ResourcePoolsBuilder.newResourcePoolsBuilder()
            .heap(1000, EntryUnit.ENTRIES)
            .offheap(10, MemoryUnit.MB)
            .disk(100, MemoryUnit.MB, true)
    )
    return withCache(alias, builder)
}

val userTypeOfIdCache: Cache<Long, User>
    get() = cacheManager.getCache(
        usersLongCacheAlias,
        Long::class.javaObjectType,
        User::class.java
    )

val userTypeOfUsernameCache: Cache<String, User>
    get() = cacheManager.getCache(
        usersStringCacheAlias,
        String::class.javaObjectType,
        User::class.java
    )

