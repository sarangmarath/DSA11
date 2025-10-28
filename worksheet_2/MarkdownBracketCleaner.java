import java.util.*;

public class MarkdownBracketCleaner {

    private static boolean isOpen(char c) {
        return "([{<".indexOf(c) != -1;
    }

    private static boolean isClose(char c) {
        return ")]}>".indexOf(c) != -1;
    }

    private static boolean isMatch(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '[' && close == ']') ||
               (open == '{' && close == '}') ||
               (open == '<' && close == '>');
    }

    public static String cleanMarkdownBrackets(String s) {
        int n = s.length();
        boolean[] keep = new boolean[n];
        Arrays.fill(keep, true);

        Deque<Integer> openStack = new ArrayDeque<>();
        Deque<Integer> starStack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (isOpen(c)) {
                openStack.push(i);
            } else if (c == '*') {
                starStack.push(i);
            } else if (isClose(c)) {
                if (!openStack.isEmpty() && isMatch(s.charAt(openStack.peek()), c)) {
                    openStack.pop();
                } else if (!starStack.isEmpty()) {
                    starStack.pop();
                } else {
                    keep[i] = false; 
                }
            }
        }

        Deque<Integer> starRight = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == '*' && keep[i]) {
                starRight.push(i);
            } else if (isOpen(c) && keep[i]) {
                if (!starRight.isEmpty()) {
                    starRight.pop(); 
                } else {
                    keep[i] = false; 
               }
            }
        }

        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '*') keep[i] = false;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (keep[i]) result.append(s.charAt(i));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(cleanMarkdownBrackets("The sum is (a[b*c] + d)"));
        System.out.println(cleanMarkdownBrackets("<[*(])>"));
        System.out.println(cleanMarkdownBrackets("hello*)("));
        System.out.println(cleanMarkdownBrackets("a*(b[c<d*>e)]f"));
    }
}
