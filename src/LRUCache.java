import java.util.HashMap;


public class LRUCache {

    private static class Node {
        String key, value;
        Node prev, next;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final HashMap<String, Node> cache;
    private final Node head; 
    private final Node tail; 


    public LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();

        head = new Node("HEAD", "HEAD");
        tail = new Node("TAIL", "TAIL");
        head.next = tail;
        tail.prev = head;
    }


    private void removeNode(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addToFront(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    private void moveToFront(Node n) {
        removeNode(n);
        addToFront(n);
    }

    private Node evict() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }


    public String get(String key) {
        Node n = cache.get(key);
        if (n == null) return null;
        moveToFront(n);
        return n.value;
    }

    public void put(String key, String value) {
        Node n = cache.get(key);
        if (n != null) {
            n.value = value;
            moveToFront(n);
            return;
        }
        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        addToFront(newNode);

        if (cache.size() > capacity) {
            Node evicted = evict();
            cache.remove(evicted.key);
            System.out.println("  [LRU] Evicted: " + evicted.key);
        }
    }

    public int size() { return cache.size(); }
    public int capacity() { return capacity; }
}