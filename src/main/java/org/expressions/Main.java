package org.expressions;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        System.out.print("Enter an expression: ");
        String expression = scanner.nextLine();

        try {
            double result = evaluator.evaluate(expression);
            System.out.println("Result: " + result);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
