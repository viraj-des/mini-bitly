import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Mini-Bitly — URL Shortener Backend
 * DS-II Mini Project | SY CSE | DES Pune University
 *
 * Data Structures used:
 *   HashMap        — O(1) URL storage & redirect
 *   Max-Heap       — Top-K analytics
 *   AVL/BST        — Category management
 *   Doubly Linked List — Access history & LRU Cache
 */
public class Main {

    // ── Shared instances ──────────────────────────────────────────────────────
    private static final URLStore store       = new URLStore();
    private static final AnalyticsHeap heap   = new AnalyticsHeap();
    private static final CategoryTree tree    = new CategoryTree();
    private static final AccessHistory history = new AccessHistory();
    private static final LRUCache lru         = new LRUCache(5);  // capacity = 5

    private static final Scanner sc = new Scanner(System.in);

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      Mini-Bitly URL Shortener        ║");
        System.out.println("║    DS-II Mini Project  |  SY CSE     ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1"  -> handleShorten();
                case "2"  -> handleRedirect();
                case "3"  -> handleTopK();
                case "4"  -> handleCategory();
                case "5"  -> handleHistory();
                case "6"  -> handleAllMappings();
                case "7"  -> handleLRUDemo();
                case "0"  -> { System.out.println("Goodbye!"); running = false; }
                default   -> System.out.println("  Invalid option. Try again.\n");
            }
        }
        sc.close();
    }

    // ── Menu ──────────────────────────────────────────────────────────────────
    private static void printMenu() {
        System.out.println();
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  1. Shorten a URL                      │");
        System.out.println("│  2. Redirect (resolve short key)       │");
        System.out.println("│  3. Top-K most visited URLs            │");
        System.out.println("│  4. Browse URLs by category            │");
        System.out.println("│  5. Access history for a short key     │");
        System.out.println("│  6. View all current mappings          │");
        System.out.println("│  7. LRU Cache demo                     │");
        System.out.println("│  0. Exit                               │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    /** 1. Shorten */
    private static void handleShorten() {
        System.out.print("  Long URL     : ");
        String longUrl = sc.nextLine().trim();
        if (longUrl.isEmpty()) { System.out.println("  URL cannot be empty."); return; }

        System.out.print("  Category     : ");
        String category = sc.nextLine().trim();
        if (category.isEmpty()) category = "general";

        String shortUrl = store.shorten(longUrl);
        // Extract the key part after the last '/'
        String shortKey = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

        // Register in heap + tree
        heap.addEntry(shortKey, longUrl, category);
        tree.insert(category, shortKey);
        lru.put(shortKey, longUrl);

        System.out.println();
        System.out.println("  ✔  Short URL  : " + shortUrl);
        System.out.println("  ✔  Short Key  : " + shortKey);
        System.out.println("  ✔  Category   : " + category);
    }

    /** 2. Redirect */
    private static void handleRedirect() {
        System.out.print("  Short key (e.g. AAAAAB) : ");
        String key = sc.nextLine().trim();

        // Try LRU cache first
        String cached = lru.get(key);
        if (cached != null) {
            System.out.println("  [CACHE HIT]  -> " + cached);
            heap.recordAccess(key);
            history.recordAccess(key);
            return;
        }

        String longUrl = store.resolve(key);
        if (longUrl == null) {
            System.out.println("  404 — Short key not found.");
            return;
        }

        System.out.println("  [CACHE MISS] Fetching from store ...");
        lru.put(key, longUrl);
        heap.recordAccess(key);
        history.recordAccess(key);
        System.out.println("  301 Redirect -> " + longUrl);
        System.out.println("  Click count  : " + heap.getClickCount(key));
    }

    /** 3. Top-K */
    private static void handleTopK() {
        System.out.print("  K (how many top URLs?): ");
        int k;
        try { k = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid number."); return; }

        List<AnalyticsHeap.URLEntry> top = heap.getTopK(k);
        if (top.isEmpty()) { System.out.println("  No data yet — shorten and visit some URLs first."); return; }

        System.out.println();
        System.out.printf("  %-3s %-10s %-8s %s%n", "#", "ShortKey", "Clicks", "Long URL");
        System.out.println("  " + "─".repeat(70));
        int rank = 1;
        for (AnalyticsHeap.URLEntry e : top) {
            System.out.printf("  %-3d %-10s %-8d %s%n", rank++, e.shortKey, e.clickCount, e.longUrl);
        }
    }

    /** 4. Category browser */
    private static void handleCategory() {
        System.out.println("  All categories (in-order):");
        List<String> cats = tree.inOrder();
        if (cats.isEmpty()) { System.out.println("  (empty)"); return; }
        cats.forEach(c -> System.out.println("    • " + c));

        System.out.print("\n  Enter category to list its URLs (or ENTER to skip): ");
        String cat = sc.nextLine().trim();
        if (!cat.isEmpty()) {
            List<String> keys = tree.search(cat);
            if (keys.isEmpty()) {
                System.out.println("  No URLs found for category: " + cat);
            } else {
                System.out.println("  URLs in category \"" + cat + "\":");
                keys.forEach(k -> System.out.println("    → " + k + "  (" + store.resolve(k) + ")"));
            }
        }
    }

    /** 5. Access history */
    private static void handleHistory() {
        System.out.print("  Short key: ");
        String key = sc.nextLine().trim();
        List<String> logs = history.getHistory(key);
        if (logs.isEmpty()) {
            System.out.println("  No access history for key: " + key);
        } else {
            System.out.println("  Access history for [" + key + "]:");
            int i = 1;
            for (String ts : logs) System.out.println("    " + i++ + ". " + ts);
        }
    }

    /** 6. All mappings */
    private static void handleAllMappings() {
        Map<String, String> all = store.getAllMappings();
        if (all.isEmpty()) { System.out.println("  No URLs shortened yet."); return; }
        System.out.printf("  %-12s %s%n", "Short Key", "Long URL");
        System.out.println("  " + "─".repeat(60));
        all.forEach((k, v) -> System.out.printf("  %-12s %s%n", k, v));
    }

    /** 7. LRU demo */
    private static void handleLRUDemo() {
        System.out.println("  LRU Cache state (capacity = " + lru.capacity() + ", size = " + lru.size() + ")");
        System.out.println("  Use options 1 & 2 to populate the cache naturally.");
        System.out.println("  Cache automatically evicts LRU entry when full.");
    }
}