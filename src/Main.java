import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final URLStore store = new URLStore();
    private static final AnalyticsHeap heap = new AnalyticsHeap();
    private static final CategoryTree tree = new CategoryTree();
    private static final AccessHistory history = new AccessHistory();
    private static final LRUCache cache = new LRUCache(5);

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("===== Mini URL Shortener (Bitly Style) =====");
        System.out.println("DS-II Mini Project | SY CSE");

        boolean isRunning = true;

        while (isRunning) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> shortenURL();
                case "2" -> redirectURL();
                case "3" -> showTopK();
                case "4" -> browseCategory();
                case "5" -> showHistory();
                case "6" -> showAllMappings();
                case "7" -> showCacheInfo();
                case "0" -> {
                    System.out.println("Exiting program...");
                    isRunning = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Shorten URL");
        System.out.println("2. Redirect URL");
        System.out.println("3. Top K URLs");
        System.out.println("4. Browse by Category");
        System.out.println("5. View History");
        System.out.println("6. All Mappings");
        System.out.println("7. LRU Cache Info");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private static void shortenURL() {
        System.out.print("Enter long URL: ");
        String longUrl = scanner.nextLine().trim();

        if (longUrl.isEmpty()) {
            System.out.println("URL cannot be empty.");
            return;
        }

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) category = "general";

        String shortUrl = store.shorten(longUrl);
        String key = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

        heap.addEntry(key, longUrl, category);
        tree.insert(category, key);
        cache.put(key, longUrl);

        System.out.println("Short URL created: " + shortUrl);
    }

    private static void redirectURL() {
        System.out.print("Enter short key: ");
        String key = scanner.nextLine().trim();

        String cached = cache.get(key);

        if (cached != null) {
            System.out.println("[Cache Hit] → " + cached);
        } else {
            String longUrl = store.resolve(key);
            if (longUrl == null) {
                System.out.println("URL not found.");
                return;
            }

            cache.put(key, longUrl);
            System.out.println("[Cache Miss] → " + longUrl);
        }

        heap.recordAccess(key);
        history.recordAccess(key);
    }

    private static void showTopK() {
        System.out.print("Enter K: ");
        int k = Integer.parseInt(scanner.nextLine());

        List<AnalyticsHeap.URLEntry> top = heap.getTopK(k);

        if (top.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        for (AnalyticsHeap.URLEntry entry : top) {
            System.out.println(entry.shortKey + " → " + entry.clickCount);
        }
    }

    private static void browseCategory() {
        List<String> categories = tree.inOrder();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        categories.forEach(System.out::println);
    }

    private static void showHistory() {
        System.out.print("Enter short key: ");
        String key = scanner.nextLine();

        List<String> logs = history.getHistory(key);

        if (logs.isEmpty()) {
            System.out.println("No history found.");
            return;
        }

        logs.forEach(System.out::println);
    }

    private static void showAllMappings() {
        Map<String, String> all = store.getAllMappings();

        all.forEach((k, v) -> System.out.println(k + " → " + v));
    }

    private static void showCacheInfo() {
        System.out.println("Cache Size: " + cache.size());
        System.out.println("Capacity: " + cache.capacity());
    }
}