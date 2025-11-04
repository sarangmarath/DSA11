import java.util.*;

public class InfixEvaluator {
    
    private static final Map<String, Integer> PRECEDENCE = Map.of(
        "+", 1, "-", 1,
        "*", 2, "/", 2, "%", 2,
        "^", 3
    );

    public static int evaluate(String expr, Map<String, Integer> env) {
        try {
            List<String> rpn = infixToRPN(expr);
            System.out.println("RPN: " + rpn);
            int result = evalRPN(rpn, env);
            System.out.println("Result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new RuntimeException("ERROR");
        }
    }

    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (Character.isLetterOrDigit(c)) {
                sb.setLength(0);
                sb.append(c);
                while (i + 1 < expr.length() && Character.isLetterOrDigit(expr.charAt(i + 1))) {
                    sb.append(expr.charAt(++i));
                }
                tokens.add(sb.toString());
            } else if ("+-*/%^(),".indexOf(c) >= 0) {
                tokens.add(String.valueOf(c));
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }
        return tokens;
    }

    private static List<String> infixToRPN(String expr) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        List<String> tokens = tokenize(expr);

        String prev = null;
        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (isFunction(token)) {
                stack.push(token);
            } else if (token.equals(",")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException("Misplaced comma");
            } else if (PRECEDENCE.containsKey(token)) {
                // Handle unary minus
                if (token.equals("-") && (prev == null || "(".equals(prev) || PRECEDENCE.containsKey(prev) || prev.equals(","))) {
                    stack.push("u-"); // unary minus
                } else {
                    while (!stack.isEmpty() && PRECEDENCE.containsKey(stack.peek()) &&
                            (isLeftAssoc(token) && PRECEDENCE.get(token) <= PRECEDENCE.get(stack.peek()) ||
                             !isLeftAssoc(token) && PRECEDENCE.get(token) < PRECEDENCE.get(stack.peek()))) {
                        output.add(stack.pop());
                    }
                    stack.push(token);
                }
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                stack.pop();
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
            prev = token;
        }

        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(") || op.equals(")")) throw new IllegalArgumentException("Mismatched parentheses");
            output.add(op);
        }

        return output;
    }

    // ---------------- Evaluate RPN ----------------
    private static int evalRPN(List<String> rpn, Map<String, Integer> env) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Integer.parseInt(token));
            } else if (isVariable(token)) {
                if (!env.containsKey(token))
                    throw new IllegalArgumentException("Missing variable: " + token);
                stack.push(env.get(token));
            } else if (token.equals("u-")) {
                stack.push(-stack.pop());
            } else if (PRECEDENCE.containsKey(token)) {
                int b = stack.pop(), a = stack.pop();
                stack.push(applyOp(a, b, token));
            } else if (isFunction(token)) {
                if (token.equals("abs")) {
                    int v = stack.pop();
                    stack.push(Math.abs(v));
                } else if (token.equals("min")) {
                    int b = stack.pop(), a = stack.pop();
                    stack.push(Math.min(a, b));
                } else if (token.equals("max")) {
                    int b = stack.pop(), a = stack.pop();
                    stack.push(Math.max(a, b));
                }
            } else {
                throw new IllegalArgumentException("Invalid token in RPN: " + token);
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Malformed expression");
        return stack.pop();
    }

    // ---------------- Helpers ----------------
    private static boolean isLeftAssoc(String op) {
        return !op.equals("^"); // exponent is right-associative
    }

    private static boolean isFunction(String s) {
        return s.equals("min") || s.equals("max") || s.equals("abs");
    }

    private static boolean isNumber(String s) {
        return s.matches("-?\\d+");
    }

    private static boolean isVariable(String s) {
        return s.matches("[a-zA-Z]\\w*") && !isFunction(s);
    }

    private static int applyOp(int a, int b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new IllegalArgumentException("Division by zero");
                yield a / b;
            }
            case "%" -> {
                if (b == 0) throw new IllegalArgumentException("Modulo by zero");
                yield a % b;
            }
            case "^" -> (int) Math.pow(a, b);
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    // ---------------- Main (Test) ----------------
    public static void main(String[] args) {
        Map<String, Integer> env1 = Map.of();
        evaluate("3 + 4 * 2 / (1 - 5) ^ 2^3", env1);

        Map<String, Integer> env2 = Map.of();
        evaluate("min(10, max(2, 3*4))", env2);

        Map<String, Integer> env3 = Map.of("x", -2, "y", -7);
        evaluate("-(x) + abs(y)", env3);

        Map<String, Integer> env4 = Map.of("a", 1);
        try {
            evaluate("a + b", env4);
        } catch (RuntimeException ignored) {}
    }
}
