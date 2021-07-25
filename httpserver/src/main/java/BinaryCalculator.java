public class BinaryCalculator {
    int func(int a, int b, Operator operator) {
        return operator.apply(a, b);
    }

    public static void main(String[] args) {
        Operator op = (x, y) -> x - y + 2;
        BinaryCalculator calculator = new BinaryCalculator();

        System.out.println(calculator.func(3, 2, new Add()));
        System.out.println(calculator.func(12, 1, new Add()));
    }


    static class Add implements Operator {

        @Override
        public int apply(int a, int b) {
            System.out.println("Inside Add");
            return a + b + 3;
        }
    }

    static class Sub implements Operator {

        @Override
        public int apply(int a, int b) {
            System.out.println("Inside Sub");
            return a - b;
        }
    }
}

@FunctionalInterface
interface Operator {
    int apply(int a, int b);
}


