import java.util.HashMap;
import java.util.Map;

public class URLStore {

    private static final String BASE_DOMAIN = "https://mini.ly/";
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final HashMap<String, String> urlMap;       // shortKey -> longUrl
    private final HashMap<String, String> reverseMap;   // longUrl  -> shortKey
    private long counter;

    public URLStore() {
        urlMap = new HashMap<>();
        reverseMap = new HashMap<>();
        counter = 0;
    }

    // Base62 encode
    private String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHARS.charAt((int)(id % 62)));
            id /= 62;
        }
        while (sb.length() < 6) sb.append('A');
        return sb.reverse().toString();
    }

    // Shorten a long URL; returns full short URL
    public String shorten(String longUrl) {
        if (reverseMap.containsKey(longUrl)) {
            return BASE_DOMAIN + reverseMap.get(longUrl);
        }
        counter++;
        String shortKey = encode(counter);
        urlMap.put(shortKey, longUrl);
        reverseMap.put(longUrl, shortKey);
        return BASE_DOMAIN + shortKey;
    }

    // Resolve a short key -> long URL (null if not found)
    public String resolve(String shortKey) {
        return urlMap.get(shortKey);
    }

    // Check existence
    public boolean exists(String shortKey) {
        return urlMap.containsKey(shortKey);
    }

    // Return all mappings (shortKey -> longUrl)
    public Map<String, String> getAllMappings() {
        return new HashMap<>(urlMap);
    }

    public int size() {
        return urlMap.size();
    }
}