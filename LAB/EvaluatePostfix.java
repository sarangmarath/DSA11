import java.util.*;

public class EvaluatePostfix {
    public static int evaluate(String expr) {
        Stack<Integer> stack = new Stack<>();
        String[] tokens = expr.split(" ");

        for (String token : tokens) {
            if (token.matches("\\d+")) {  // if operand
                stack.push(Integer.parseInt(token));
            } else {  // operator
                int b = stack.pop();
                int a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) {
        String expr = "6 3 2 + *";
        System.out.println("Expression: " + expr);
        System.out.println("Result: " + evaluate(expr));
    }
}
