/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.core.utils.EnumUtil
import me.deecaad.core.utils.StringUtil

class FloatModifier : Serializer<FloatModifier> {

    lateinit var operation: Operation
    var amount: Float = 0f

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(operation: Operation, amount: Float) {
        this.operation = operation
        this.amount = amount
    }

    fun apply(num: Float): Float {
        return operation.evaluate(num, amount)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): FloatModifier {
        val line = data.of().assertExists().get(String::class.java).get()
        val split = StringUtil.split(line)
        if (split.size != 2)
            throw data.exception(null, "Invalid input: '$line'", "Expected 2 arguments, but got " + split.size, "Valid Format: <ADD/MULTIPLY/SET> <Decimal>")

        val amount: Float
        val operation: Operation

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation::class.java, split[0]).orElseThrow()
            amount = split[1].toFloat()
        } catch (ex: NoSuchElementException) {
            throw SerializerException.builder()
                .locationRaw(data.of().location)
                .buildInvalidEnumOption(split[0], Operation::class.java)
        } catch (ex: NumberFormatException) {
            throw SerializerException.builder()
                .locationRaw(data.of().location)
                .apply { ex.message?.let { addMessage(it) } }
                .buildInvalidType("decimal", split[1])
        }
        return FloatModifier(operation, amount)
    }
}