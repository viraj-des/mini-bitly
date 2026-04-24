
import java.util.ArrayList;
import java.util.List;

public class CategoryTree {

    // AVL Tree Node
    private static class TreeNode {
        String category;
        List<String> urls;     // list of short keys in this category
        TreeNode left, right;
        int height;

        TreeNode(String category) {
            this.category = category;
            this.urls = new ArrayList<>();
            this.height = 1;
        }
    }

    private TreeNode root;

    // ── AVL Helpers ──────────────────────────────────────────────────────────

    private int height(TreeNode n) {
        return n == null ? 0 : n.height;
    }

    private int balanceFactor(TreeNode n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private void updateHeight(TreeNode n) {
        if (n != null)
            n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private TreeNode rotateRight(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private TreeNode balance(TreeNode n) {
        updateHeight(n);
        int bf = balanceFactor(n);

        if (bf > 1) {
            if (balanceFactor(n.left) < 0) n.left = rotateLeft(n.left); // LR
            return rotateRight(n);
        }
        if (bf < -1) {
            if (balanceFactor(n.right) > 0) n.right = rotateRight(n.right); // RL
            return rotateLeft(n);
        }
        return n;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    // Insert a short key under a category
    public void insert(String category, String shortKey) {
        root = insert(root, category, shortKey);
    }

    private TreeNode insert(TreeNode node, String category, String shortKey) {
        if (node == null) {
            TreeNode n = new TreeNode(category);
            n.urls.add(shortKey);
            return n;
        }
        int cmp = category.compareToIgnoreCase(node.category);
        if (cmp < 0) {
            node.left = insert(node.left, category, shortKey);
        } else if (cmp > 0) {
            node.right = insert(node.right, category, shortKey);
        } else {
            // Same category — just append the URL
            if (!node.urls.contains(shortKey))
                node.urls.add(shortKey);
            return node;
        }
        return balance(node);
    }

    // Search for all short keys in a category
    public List<String> search(String category) {
        TreeNode node = search(root, category);
        return node == null ? new ArrayList<>() : new ArrayList<>(node.urls);
    }

    private TreeNode search(TreeNode node, String category) {
        if (node == null) return null;
        int cmp = category.compareToIgnoreCase(node.category);
        if (cmp < 0) return search(node.left, category);
        if (cmp > 0) return search(node.right, category);
        return node;
    }

    // In-order traversal — returns all categories alphabetically
    public List<String> inOrder() {
        List<String> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(TreeNode node, List<String> result) {
        if (node == null) return;
        inOrder(node.left, result);
        result.add(node.category + " (" + node.urls.size() + " url(s))");
        inOrder(node.right, result);
    }
}