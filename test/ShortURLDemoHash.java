public class ShortURLHashDemo {

    static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(int num) {
        StringBuilder shortURL = new StringBuilder();

        if (num < 0) num = -num;

        while (num > 0) {
            shortURL.append(chars.charAt(num % 62));
            num /= 62;
        }

        return shortURL.reverse().toString();
    }

    public static int hashSum(String url) {
        int sum = 0;

        for (char c : url.toCharArray()) {
            sum += c;
        }

        return sum;
    }

    public static int hashPolynomial(String url) {
        int hash = 0;
        int p = 31;
        int m = 1000000007;

        for (char c : url.toCharArray()) {
            hash = (hash * p + c) % m;
        }

        return hash;
    }

    public static int hashJava(String url) {
        return url.hashCode();
    }

    public static void main(String[] args) {

        String[] urls = {
            "https://google.com",
            "https://youtube.com",
            "https://chatgpt.com",
            "https://github.com"
        };

        System.out.println("---- SHORT URL USING DIFFERENT HASH FUNCTIONS ----\n");

        for (String url : urls) {

            int h1 = hashSum(url);
            int h2 = hashPolynomial(url);
            int h3 = hashJava(url);

            String s1 = encode(h1);
            String s2 = encode(h2);
            String s3 = encode(h3);

            System.out.println("Original URL: " + url);
            System.out.println("HashSum ShortURL: " + s1);
            System.out.println("Polynomial ShortURL: " + s2);
            System.out.println("Java hashCode ShortURL: " + s3);
            System.out.println("-----------------------------------");
        }
    }
}