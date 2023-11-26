package cache.strategy

class LruStrategy<K, V> : EvictionStrategy<K, V> {

    private class Node<K, V>(var key: K?, var value: V?) {
        var prev: Node<K, V>? = null
        var next: Node<K, V>? = null
    }

    private val head = Node<K, V>(null, null)
    private val tail = Node<K, V>(null, null)
    private val nodeMap: MutableMap<K, Node<K, V>> = mutableMapOf()

    init {
        head.next = tail
        tail.prev = head
    }

    override fun evict(cache: MutableMap<K, V>) {
        val lru = head.next
        if (lru != null && lru != tail) {
            lru.key?.let {
                cache.remove(it)
                nodeMap.remove(it)
            }
            removeNode(lru)
        }
    }

    fun recordAccess(key: K, value: V) {
        var node = nodeMap[key]
        if (node == null) {
            node = Node(key, value)
            nodeMap[key] = node
            addNode(node)
        } else {
            removeNode(node)
            addNode(node)
        }
    }

    private fun addNode(node: Node<K, V>) {
        val last = tail.prev
        last?.next = node
        node.prev = last
        node.next = tail
        tail.prev = node
    }

    private fun removeNode(node: Node<K, V>) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }
}