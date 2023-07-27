package me.deecaad.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.file.*
import me.deecaad.core.utils.EnumUtil
import me.deecaad.core.utils.StringUtil
import kotlin.math.roundToInt

class IntegerModifier : Serializer<IntegerModifier> {

    var operation: Operation = Operation.ADD
    var amount: Int = 0
    var multiplier: Double = 0.0

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(operation: Operation, amount: Number) {
        this.operation = operation

        // Allow decimals for multipliers, then we'll round
        if (operation == Operation.MULTIPLY)
            multiplier = amount.toDouble()
        else
            this.amount = amount.toInt()
    }

    fun apply(num: Int): Int {
        if (operation == Operation.MULTIPLY)
            return operation.evaluate(num.toDouble(), multiplier).roundToInt()

        return operation.evaluate(num, amount)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): IntegerModifier {
        val line = data.config.getString(data.key)
        val split = StringUtil.split(line)
        if (split.size != 2)
            throw data.exception(null, "Invalid input: '$line'", "Expected 2 arguments, but got " + split.size, "Valid Format: <ADD/MULTIPLY> <Integer>")

        val amount: Number
        val operation: Operation

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation::class.java, split[0]).orElseThrow()
            amount = if (operation == Operation.MULTIPLY) split[1].toDouble() else split[1].toInt()
        } catch (ex: NoSuchElementException) {
            throw SerializerEnumException(data.serializer, Operation::class.java, split[0], false, data.of().location)
        } catch (ex: NumberFormatException) {
            throw SerializerTypeException(data.serializer, Int::class.java, String::class.java, split[1], data.of().location)
        }
        return IntegerModifier(operation, amount)
    }
}