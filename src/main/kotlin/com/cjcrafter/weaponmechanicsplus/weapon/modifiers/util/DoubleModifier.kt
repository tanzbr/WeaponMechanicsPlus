package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.file.*
import me.deecaad.core.utils.EnumUtil
import me.deecaad.core.utils.StringUtil

class DoubleModifier : Serializer<DoubleModifier> {

    var operation = Operation.ADD
    var amount = 0.0

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(operation: Operation, amount: Double) {
        this.operation = operation
        this.amount = amount
    }

    fun apply(num: Double): Double {
        return operation.evaluate(num, amount)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): DoubleModifier {
        val line = data.of().assertExists().assertType(String::class.java).get<String>()
        val split = StringUtil.split(line)
        if (split.size != 2)
            throw data.exception(null, "Invalid input: '$line'", "Expected 2 arguments, but got " + split.size, "Valid Format: <ADD/MULTIPLY> <Decimal>")

        val amount: Double
        val operation: Operation

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation::class.java, split[0]).orElseThrow()
            amount = split[1].toDouble()
        } catch (ex: NoSuchElementException) {
            throw SerializerEnumException(data.serializer, Operation::class.java, split[0], false, data.of().location)
        } catch (ex: NumberFormatException) {
            throw SerializerTypeException(data.serializer, Double::class.java, String::class.java, split[1], data.of().location)
        }
        return DoubleModifier(operation, amount)
    }
}