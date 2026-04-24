import java.util.*;

public class AnalyticsHeap {

    // Inner class representing a URL entry with click count
    public static class URLEntry implements Comparable<URLEntry> {
        String shortKey;
        String longUrl;
        String category;
        int clickCount;

        public URLEntry(String shortKey, String longUrl, String category) {
            this.shortKey = shortKey;
            this.longUrl = longUrl;
            this.category = category;
            this.clickCount = 0;
        }

        @Override
        public int compareTo(URLEntry other) {
            return Integer.compare(this.clickCount, other.clickCount);
        }

        @Override
        public String toString() {
            return String.format("[%s] -> %s | Clicks: %d | Category: %s",
                    shortKey, longUrl, clickCount, category);
        }
    }

    // Max-Heap (reverse order comparator)
    private final PriorityQueue<URLEntry> heap;
    // Index map for O(1) access to entries
    private final HashMap<String, URLEntry> index;

    public AnalyticsHeap() {
        heap = new PriorityQueue<>(Collections.reverseOrder());
        index = new HashMap<>();
    }

    // Register a new URL entry
    public void addEntry(String shortKey, String longUrl, String category) {
        URLEntry entry = new URLEntry(shortKey, longUrl, category);
        index.put(shortKey, entry);
        heap.offer(entry);
    }

    // Record an access / click for a short key
    public void recordAccess(String shortKey) {
        URLEntry entry = index.get(shortKey);
        if (entry == null) return;

        // Remove, update, re-insert to maintain heap property
        heap.remove(entry);
        entry.clickCount++;
        heap.offer(entry);
    }

    // Get top-K most accessed URLs
    public List<URLEntry> getTopK(int k) {
        List<URLEntry> result = new ArrayList<>();
        // Drain into temp list, then restore
        List<URLEntry> temp = new ArrayList<>();

        int count = Math.min(k, heap.size());
        for (int i = 0; i < count; i++) {
            URLEntry top = heap.poll();
            result.add(top);
            temp.add(top);
        }
        // Restore heap
        heap.addAll(temp);
        return result;
    }

    // Get click count for a specific short key
    public int getClickCount(String shortKey) {
        URLEntry entry = index.get(shortKey);
        return entry == null ? 0 : entry.clickCount;
    }

    public int size() {
        return heap.size();
    }
}