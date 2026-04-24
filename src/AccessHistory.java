import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccessHistory {

    private static class Node {
        String timestamp;
        Node prev, next;

        Node(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    private static class AccessList {
        Node head, tail;
        int size;

        void addAccess(String timestamp) {
            Node n = new Node(timestamp);
            if (head == null) {
                head = tail = n;
            } else {
                tail.next = n;
                n.prev = tail;
                tail = n;
            }
            size++;
        }

        List<String> toList() {
            List<String> out = new ArrayList<>();
            Node cur = head;
            while (cur != null) {
                out.add(cur.timestamp);
                cur = cur.next;
            }
            return out;
        }
    }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HashMap<String, AccessList> historyMap;

    public AccessHistory() {
        historyMap = new HashMap<>();
    }

    public void recordAccess(String shortKey) {
        historyMap.computeIfAbsent(shortKey, k -> new AccessList())
                  .addAccess(LocalDateTime.now().format(FMT));
    }

    public List<String> getHistory(String shortKey) {
        AccessList list = historyMap.get(shortKey);
        return list == null ? new ArrayList<>() : list.toList();
    }

    public int getAccessCount(String shortKey) {
        AccessList list = historyMap.get(shortKey);
        return list == null ? 0 : list.size;
    }
}