package cache.strategy

interface EvictionStrategy<K, V> {
    fun evict(cache: MutableMap<K, V>)
}