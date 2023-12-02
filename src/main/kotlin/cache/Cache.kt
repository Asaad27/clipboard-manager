package cache

import cache.strategy.EvictionStrategy
import cache.strategy.LruStrategy
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class Cache<K, V>(private var evictionStrategy: EvictionStrategy<K, V>, private val capacity: Int = 500) {
    private val cache: MutableMap<K, V> = ConcurrentHashMap()
    private val logger = LoggerFactory.getLogger(javaClass)

    fun setEvictionStrategy(evictionStrategy: EvictionStrategy<K, V>) {
        this.evictionStrategy = evictionStrategy
    }

    fun get(key: K): V? {
        return cache[key]?.also {
            logger.debug("cache hit for: {} and value: {}", key, it)
            recordAccess(key, it)
        }
    }

    fun put(key: K, value: V?) {
        if (value == null) return
        if (cache.size >= capacity) {
            evictionStrategy.evict(cache)
        }
        cache[key] = value
        recordAccess(key, value)
    }

    fun remove(key: K) {
        cache.remove(key)
    }

    private fun recordAccess(key: K, it: V) {
        if (evictionStrategy is LruStrategy) {
            (evictionStrategy as LruStrategy<K, V>).recordAccess(key, it)
        }
    }
}