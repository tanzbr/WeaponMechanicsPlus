package me.deecaad.weaponmechanicsplus.weapon.modifiers.util;

/**
 * Outlines a simple math operation, like add/multiply.
 */
public enum Operation {

    ADD {
        @Override
        public double evaluate(double a, double b) {
            return a + b;
        }

        @Override
        public int evaluate(int a, int b) {
            return a + b;
        }
    }, MULTIPLY {
        @Override
        public double evaluate(double a, double b) {
            return a * b;
        }

        @Override
        public int evaluate(int a, int b) {
            return a * b;
        }
    }, SET {
        @Override
        public double evaluate(double a, double b) {
            return b;
        }

        @Override
        public int evaluate(int a, int b) {
            return b;
        }
    };

    public abstract double evaluate(double a, double b);

    public abstract int evaluate(int a, int b);
}