package me.deecaad.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.file.*
import me.deecaad.core.utils.EnumUtil
import me.deecaad.core.utils.StringUtil

class IntegerModifier : Serializer<IntegerModifier> {

    var operation: Operation = Operation.ADD
    var amount = 0 // TODO let people use double as multiplier

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(operation: Operation, amount: Int) {
        this.operation = operation
        this.amount = amount
    }

    fun apply(num: Int): Int {
        return operation.evaluate(num, amount)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): IntegerModifier {
        val line = data.config.getString(data.key)
        val split = StringUtil.split(line)
        if (split.size != 2)
            throw data.exception(null, "Invalid input: '$line'", "Expected 2 arguments, but got " + split.size, "Valid Format: <ADD/MULTIPLY> <Integer>")

        val amount: Int
        val operation: Operation

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation::class.java, split[0]).orElseThrow()
            amount = split[1].toInt()
        } catch (ex: NoSuchElementException) {
            throw SerializerEnumException(data.serializer, Operation::class.java, split[0], false, data.of().location)
        } catch (ex: NumberFormatException) {
            throw SerializerTypeException(data.serializer, Int::class.java, String::class.java, split[1], data.of().location)
        }
        return IntegerModifier(operation, amount)
    }
}