package org.expressions;

import java.util.*;

/**
 * A utility class for evaluating mathematical expressions with variables.
 * Supports basic arithmetic operations (+, -, *, /), parentheses, and variable substitution.
 */
public class ExpressionEvaluator {

    private Map<String, Double> variables = new HashMap<>();

    /**
     * Evaluates a mathematical expression.
     * If the expression contains variables, their values will be requested from the user.
     *
     * @param expression the mathematical expression to evaluate
     * @return the result of the evaluation
     * @throws IllegalArgumentException if the expression is invalid
     */
    public double evaluate(String expression) {
        try {
            validateExpression(expression);

            Set<String> variablesToFind = findVariables(expression);
            requestVariableValues(variablesToFind);
            return parseExpression(new Tokenizer(expression));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression: " + expression, e);
        }
    }

    /**
     * Validates the syntax of the given expression.
     *
     * @param expression the expression to validate
     * @throws IllegalArgumentException if the expression is invalid
     */
    private void validateExpression(String expression) {
        String trimmed = expression.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }

        if (!trimmed.matches("[a-zA-Z0-9+\\-*/().\\s]+")) {
            throw new IllegalArgumentException("Expression contains invalid characters");
        }

        if (trimmed.matches(".*([a-zA-Z0-9.]+\\s+[a-zA-Z0-9.]+).*")) {
            throw new IllegalArgumentException("Expression contains two operands without an operator");
        }

        if (trimmed.matches(".*[+\\-*/]{2,}.*") || trimmed.matches(".*[+\\-*/]\\s*[)].*") || trimmed.matches(".*[(]\\s*[+\\-*/].*")) {
            throw new IllegalArgumentException("Expression contains invalid operator usage");
        }

        int openParentheses = 0;
        for (char c : trimmed.toCharArray()) {
            if (c == '(') {
                openParentheses++;
            } else if (c == ')') {
                openParentheses--;
            }

            if (openParentheses < 0) {
                throw new IllegalArgumentException("Expression contains unmatched closing parenthesis");
            }
        }
        if (openParentheses != 0) {
            throw new IllegalArgumentException("Expression contains unmatched opening parenthesis");
        }
    }

    /**
     * Finds all variable names in the given expression.
     *
     * @param expression the expression to analyze
     * @return a set of variable names
     */
    private Set<String> findVariables(String expression) {
        Set<String> variables = new HashSet<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isLetter(c)) {
                StringBuilder variable = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    variable.append(expression.charAt(i));
                    i++;
                }
                variables.add(variable.toString());
            }
        }
        return variables;
    }

    /**
     * Requests the user to input values for each variable.
     *
     * @param variablesToFind the set of variables to request values for
     */
    private void requestVariableValues(Set<String> variablesToFind) {
        Scanner scanner = new Scanner(System.in);
        for (String variable : variablesToFind) {
            if (!variables.containsKey(variable)) {
                System.out.print("Enter value for variable " + variable + ": ");
                double value = scanner.nextDouble();
                variables.put(variable, value);
            }
        }
    }

    /**
     * Parses and evaluates an expression using a tokenizer.
     *
     * @param tokenizer the tokenizer for the expression
     * @return the result of the evaluation
     */
    private double parseExpression(Tokenizer tokenizer) {
        double value = parseTerm(tokenizer);
        while (tokenizer.hasNext()) {
            char op = tokenizer.peek();
            if (op == '+' || op == '-') {
                tokenizer.next();
                double nextTerm = parseTerm(tokenizer);
                value = (op == '+') ? value + nextTerm : value - nextTerm;
            } else {
                break;
            }
        }
        return value;
    }

    private double parseTerm(Tokenizer tokenizer) {
        double value = parseFactor(tokenizer);
        while (tokenizer.hasNext()) {
            char op = tokenizer.peek();
            if (op == '*' || op == '/') {
                tokenizer.next();
                double nextFactor = parseFactor(tokenizer);
                value = (op == '*') ? value * nextFactor : value / nextFactor;
            } else {
                break;
            }
        }
        return value;
    }

    private double parseFactor(Tokenizer tokenizer) {
        if (!tokenizer.hasNext()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }
        char next = tokenizer.peek();
        if (Character.isDigit(next) || next == '.') {
            return tokenizer.parseNumber();
        } else if (next == '(') {
            tokenizer.next(); // Consume '('
            double value = parseExpression(tokenizer);
            if (!tokenizer.hasNext() || tokenizer.next() != ')') {
                throw new IllegalArgumentException("Missing closing parenthesis");
            }
            return value;
        } else if (Character.isLetter(next)) {
            String name = tokenizer.parseName();
            if (variables.containsKey(name)) {
                return variables.get(name);
            } else {
                throw new IllegalArgumentException("Variable " + name + " is undefined");
            }
        } else if (next == '-') {
            tokenizer.next();
            return -parseFactor(tokenizer);
        } else {
            throw new IllegalArgumentException("Unexpected character: " + next);
        }
    }

    private static class Tokenizer {
        private final String expression;
        private int position = 0;

        public Tokenizer(String expression) {
            this.expression = expression.replaceAll("\\s", "");
        }

        public boolean hasNext() {
            return position < expression.length();
        }

        public char peek() {
            return expression.charAt(position);
        }

        public char next() {
            return expression.charAt(position++);
        }

        public double parseNumber() {
            int start = position;
            while (hasNext() && (Character.isDigit(peek()) || peek() == '.')) {
                next();
            }
            return Double.parseDouble(expression.substring(start, position));
        }

        public String parseName() {
            int start = position;
            while (hasNext() && Character.isLetter(peek())) {
                next();
            }
            return expression.substring(start, position);
        }
    }
}
