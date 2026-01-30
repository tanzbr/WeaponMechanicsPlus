/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

/**
 * Outlines a simple math operation, like add/multiply.
 */
enum class Operation {
    ADD {
        override fun evaluate(a: Double, b: Double): Double {
            return a + b
        }

        override fun evaluate(a: Float, b: Float): Float {
            return a + b
        }

        override fun evaluate(a: Int, b: Int): Int {
            return a + b
        }
    },
    MULTIPLY {
        override fun evaluate(a: Double, b: Double): Double {
            return a * b
        }

        override fun evaluate(a: Float, b: Float): Float {
            return a * b
        }

        override fun evaluate(a: Int, b: Int): Int {
            return a * b
        }
    },
    SET {
        override fun evaluate(a: Double, b: Double): Double {
            return b
        }

        override fun evaluate(a: Float, b: Float): Float {
            return b
        }

        override fun evaluate(a: Int, b: Int): Int {
            return b
        }
    };

    abstract fun evaluate(a: Double, b: Double): Double
    abstract fun evaluate(a: Float, b: Float): Float
    abstract fun evaluate(a: Int, b: Int): Int
}