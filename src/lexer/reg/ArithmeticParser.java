package lexer.reg;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ArithmeticParser {

    public static void main(String[] args) {
        ArithmeticParser parser = new ArithmeticParser();
        System.out.println(parser.cal(parser.infixToPostfix("1+2")));
        System.out.println(parser.cal(parser.infixToPostfix("(1+23 *3)-(-2 + --4^2^1*3)")));
        System.out.println();

    }

    public Number cal(List<Ele> eles) {
        Stack<Operand> numStack = new Stack<>();
        for (Ele ele : eles) {
            ele.cal(numStack);
        }
        if (numStack.size() != 1) {
            throw new IllegalArgumentException("wrong argument.");
        }
        return numStack.pop().getValue();
    }

    public List<Ele> infixToPostfix(String expr) {
        List<Ele> result = new ArrayList<>();
        Stack<Operator> opStack = new Stack<>();
        StringBuilder numBuilder = new StringBuilder();
        int len = expr.length();
        for (int i = 0; i < len; i++) {
            char c = expr.charAt(i);
            Operator pop;
            Operator op;
            switch (c) {
                case ' ' -> outputNumber(result, numBuilder);
                case '(' -> opStack.push(new LeftBrace());
                case ')' -> {
                    outputNumber(result, numBuilder);
                    while (!((pop = opStack.pop()) instanceof LeftBrace)) {
                        result.add(pop);
                    }
                }
                case '+' -> {
                    outputNumber(result, numBuilder);
                    Character pc = previousChar(expr, i);
                    if (pc == ')' || pc == '.' || (pc >= '0' && pc <= '9')) {
                        op = new Add();
                    } else {
                        op = new AddS();
                    }
                    pushIn(result, opStack, op);
                }
                case '-' -> {
                    outputNumber(result, numBuilder);
                    Character pc = previousChar(expr, i);
                    if (pc == ')' || pc == '.' || (pc >= '1' && pc <= '9')) {
                        op = new Sub();
                    } else {
                        op = new SubS();
                    }
                    pushIn(result, opStack, op);
                }
                case '*' -> {
                    outputNumber(result, numBuilder);
                    op = new Mul();
                    pushIn(result, opStack, op);
                }
                case '/' -> {
                    outputNumber(result, numBuilder);
                    op = new Div();
                    pushIn(result, opStack, op);
                }
                case '^' -> {
                    outputNumber(result, numBuilder);
                    op = new Pow();
                    pushIn(result, opStack, op);
                }
                default -> numBuilder.append(c);
            }
        }
        outputNumber(result, numBuilder);
        while (!opStack.isEmpty()) {
            result.add(opStack.pop());
        }
        return result;
    }

    private Character previousChar(String expr, int i) {
        Character pc = null;
        for (int j = i - 1; j >=0; j--) {
            if (expr.charAt(j) != ' ') {
               pc =  expr.charAt(j);
               break;
            }
        }
        return pc;
    }

    private void outputNumber(List<Ele> result, StringBuilder numBuilder) {
        if (numBuilder.length() > 0) {
            String s = numBuilder.toString();
            if (s.contains(".")) {
                result.add(new FloatValue(Float.valueOf(s)));
            } else {
                result.add(new IntValue(Integer.valueOf(s)));
            }
            numBuilder.setLength(0);
        }
    }

    private void pushIn(List<Ele> result, Stack<Operator> opStack, Operator rightOp) {
        while (!opStack.isEmpty() && !rightOp.preced(opStack.peek())) {
            result.add(opStack.pop());
        }
        opStack.add(rightOp);
    }

    interface Ele {
        void cal(Stack<Operand> numStack);
    }

    static abstract class Operator implements Ele {
        String name;

        public Operator(String name) {
            this.name = name;
        }

        boolean preced(Operator leftOp) {
            if (this.order() != leftOp.order()) {
                return this.order() > leftOp.order();
            } else {
                return this.assoc();
            }
        }

        abstract int order();

        // false=left associativity; true=right associativity
        protected boolean assoc() {
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class Add extends Operator {

        public Add() {
            super("+");
        }

        @Override
        int order() {
            return 1;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            Operand p2 = new IntValue(0);
            if (!numStack.isEmpty()) {
                p2 = numStack.pop();
            }

            if (p1 instanceof IntValue && p2 instanceof IntValue) {
                IntValue intValue = new IntValue(((IntValue) p2).getValue() + ((IntValue) p1).getValue());
                numStack.push(intValue);
            } else {
                FloatValue floatValue = new FloatValue(p2.getValue().floatValue() + p1.getValue().floatValue());
                numStack.push(floatValue);
            }
        }
    }

    static class AddS extends Operator {

        public AddS() {
            super("+");
        }

        @Override
        int order() {
            return 2;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
        }
    }

    static class Sub extends Operator {
        public Sub() {
            super("-");
        }

        @Override
        int order() {
            return 1;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            Operand p2 = new IntValue(0);
            if (!numStack.isEmpty()) {
                p2 = numStack.pop();
            }
            if (p1 instanceof IntValue && p2 instanceof IntValue) {
                IntValue intValue = new IntValue(((IntValue) p2).getValue() - ((IntValue) p1).getValue());
                numStack.push(intValue);
            } else {
                FloatValue floatValue = new FloatValue(p2.getValue().floatValue() - p1.getValue().floatValue());
                numStack.push(floatValue);
            }

        }
    }

    static class SubS extends Operator {

        public SubS() {
            super("-");
        }

        @Override
        int order() {
            return 2;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            if (p1 instanceof IntValue) {
                numStack.push(new IntValue(-p1.getValue().intValue()));
            } else {
                numStack.push(new FloatValue(-p1.getValue().floatValue()));
            }
        }
    }

    static class Mul extends Operator {
        public Mul() {
            super("*");
        }

        @Override
        int order() {
            return 10;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            Operand p2 = numStack.pop();
            if (p1 instanceof IntValue && p2 instanceof IntValue) {
                IntValue intValue = new IntValue(((IntValue) p2).getValue() * ((IntValue) p1).getValue());
                numStack.push(intValue);
            } else {
                FloatValue floatValue = new FloatValue(p2.getValue().floatValue() * p1.getValue().floatValue());
                numStack.push(floatValue);
            }

        }

    }

    static class Div extends Operator {
        public Div() {
            super("/");
        }

        @Override
        int order() {
            return 10;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            Operand p2 = numStack.pop();
            if (p1 instanceof IntValue && p2 instanceof IntValue) {
                IntValue intValue = new IntValue(((IntValue) p2).getValue() / ((IntValue) p1).getValue());
                numStack.push(intValue);
            } else {
                FloatValue floatValue = new FloatValue(p2.getValue().floatValue() / p1.getValue().floatValue());
                numStack.push(floatValue);
            }

        }

    }

    static class Pow extends Operator {
        public Pow() {
            super("^");
        }

        @Override
        int order() {
            return 20;
        }

        @Override
        protected boolean assoc() {
            return true;
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            Operand p1 = numStack.pop();
            Operand p2 = numStack.pop();
            double pow = Math.pow(p2.getValue().doubleValue(), p1.getValue().doubleValue());
            if (p1 instanceof IntValue && p2 instanceof IntValue) {
                numStack.push(new FloatValue((float) pow));
            } else {
                numStack.push(new IntValue((int) pow));
            }
        }
    }

    static class LeftBrace extends Operator {
        public LeftBrace() {
            super("("); // order = 0 means start a new expr
        }

        @Override
        int order() {
            return 0;
        }

        @Override
        public void cal(Stack<Operand> numStack) {

        }
    }

    static abstract class Operand implements Ele {
        Number value;

        public Operand(Number value) {
            this.value = value;
        }

        protected Number getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    static class IntValue extends Operand {

        public IntValue(Integer value) {
            super(value);
        }

        @Override
        protected Integer getValue() {
            return (Integer) super.getValue();
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            numStack.push(this);
        }
    }

    static class FloatValue extends Operand {

        public FloatValue(Float value) {
            super(value);
        }

        @Override
        protected Float getValue() {
            return (Float) super.getValue();
        }

        @Override
        public void cal(Stack<Operand> numStack) {
            numStack.push(this);
        }
    }
}
