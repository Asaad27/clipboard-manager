package cache

import cache.strategy.EvictionStrategy
import cache.strategy.LruStrategy
import java.util.concurrent.ConcurrentHashMap

class Cache<K, V>(private var evictionStrategy: EvictionStrategy<K, V>, private val capacity: Int) {
    private val cache: MutableMap<K, V> = ConcurrentHashMap()

    fun setEvictionStrategy(evictionStrategy: EvictionStrategy<K, V>) {
        this.evictionStrategy = evictionStrategy
    }

    fun get(key: K): V? {
        return cache[key]?.also {
            recordAccess(key, it)
        }
    }

    fun put(key: K, value: V) {
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