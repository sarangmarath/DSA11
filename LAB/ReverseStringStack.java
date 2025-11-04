import java.util.*;

public class ReverseStringStack {
    public static String reverse(String input) {
        Stack<Character> stack = new Stack<>();

        for (char ch : input.toCharArray()) {
            stack.push(ch);
        }

        StringBuilder reversed = new StringBuilder();
        while (!stack.isEmpty()) {
            reversed.append(stack.pop());
        }

        return reversed.toString();
    }

    public static void main(String[] args) {
        String input = "HELLO";
        System.out.println("Input: " + input);
        System.out.println("Reversed: " + reverse(input));
    }
}
