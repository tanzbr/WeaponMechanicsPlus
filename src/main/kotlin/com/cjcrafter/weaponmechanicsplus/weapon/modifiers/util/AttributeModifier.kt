package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.compatibility.nbt.NBTCompatibility.AttributeSlot
import me.deecaad.core.file.SerializeData
import me.deecaad.core.utils.AttributeType
import me.deecaad.core.utils.EnumUtil

class AttributeModifier(
    val attribute: AttributeType,
    val slot: AttributeSlot? = null,
    var amount: Double = 0.0,
): Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeModifier

        if (attribute != other.attribute) return false
        if (slot != other.slot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attribute.hashCode()
        result = 31 * result + (slot?.hashCode() ?: 0)
        return result
    }

    override fun clone(): AttributeModifier {
        return super.clone() as AttributeModifier;
    }

    companion object {

        fun flatten(lists: List<List<AttributeModifier>>): MutableSet<AttributeModifier> {
            val flattened = HashMap<AttributeModifier, AttributeModifier>()

            for (list in lists) {
                for (modifier in list) {
                    if (flattened.containsKey(modifier)) {
                        flattened[modifier]!!.amount += modifier.amount
                        continue
                    }

                    flattened[modifier] = modifier.clone()
                }
            }

            return flattened.keys
        }

        fun SerializeData.ConfigListAccessor.getAttributeModifiers(): List<AttributeModifier> {
            addArgument(AttributeType::class.java, true)
            addArgument(Double::class.java, true)
            addArgument(AttributeSlot::class.java, false)
            assertList()

            val temp = ArrayList<AttributeModifier>()
            for (split in get()) {
                val attribute = EnumUtil.parseEnums(AttributeType::class.java, split[0])[0]
                val slot = if (split.size > 2) EnumUtil.parseEnums(AttributeSlot::class.java, split[2])[0] else null
                val amount = split[1].toDouble()

                temp.add(AttributeModifier(attribute, slot, amount))
            }

            return temp
        }
    }
}
