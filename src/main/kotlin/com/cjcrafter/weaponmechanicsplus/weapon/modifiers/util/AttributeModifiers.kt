package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.core.file.simple.ByNameSerializer
import me.deecaad.core.file.simple.DoubleSerializer
import me.deecaad.core.file.simple.EnumValueSerializer
import me.deecaad.core.file.simple.RegistryValueSerializer
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.meta.ItemMeta

class AttributeModifiers: Serializer<AttributeModifiers> {

    lateinit var attributes: Map<Attribute, AttributeModifier>

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(attributes: Map<Attribute, AttributeModifier>) {
        this.attributes = attributes
    }

    /**
     * Adds the attribute modifiers to the given item.
     *
     * Before calling this method, it might be a good idea to call
     * [stripAllAttributeModifiers].
     */
    fun addTo(meta: ItemMeta) {
        for ((attribute, modifier) in attributes) {
            meta.addAttributeModifier(attribute, modifier)
        }
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): AttributeModifiers {

        // TODO: Spigot will surely improve this
        val slotGroupsByName: MutableMap<String, EquipmentSlotGroup> = mutableMapOf(
            "any" to EquipmentSlotGroup.ANY,
            "mainhand" to EquipmentSlotGroup.MAINHAND,
            "offhand" to EquipmentSlotGroup.OFFHAND,
            "hand" to EquipmentSlotGroup.HAND,
            "head" to EquipmentSlotGroup.HEAD,
            "chest" to EquipmentSlotGroup.CHEST,
            "legs" to EquipmentSlotGroup.LEGS,
            "feet" to EquipmentSlotGroup.FEET,
            "armor" to EquipmentSlotGroup.ARMOR
        )

        val splitTempData = data.ofList()
            .addArgument(RegistryValueSerializer(Attribute::class.java, true))
            .addArgument(DoubleSerializer())
            .requireAllPreviousArgs()
            .addArgument(ByNameSerializer(EquipmentSlotGroup::class.java, slotGroupsByName))
            .addArgument(EnumValueSerializer(AttributeModifier.Operation::class.java, false))
            .assertExists()
            .assertList()

        val builtAttributes = mutableMapOf<Attribute, AttributeModifier>()
        for (split in splitTempData) {
            val parsedAttributes = split[0].get() as List<Attribute>
            val amount = split[1].get() as Double
            val slot = split[2].orElse(EquipmentSlotGroup.ANY) as EquipmentSlotGroup
            val operation = (split[3].orElse(listOf(AttributeModifier.Operation.ADD_NUMBER)) as List<AttributeModifier.Operation>).first()

            for (attribute in parsedAttributes) {
                val key = NamespacedKey(WeaponMechanicsPlus.getInstance(), attribute.key.key + "-" + slot)
                val modifier = AttributeModifier(key, amount, operation, slot)
                builtAttributes[attribute] = modifier
            }
        }

        return AttributeModifiers(builtAttributes)
    }

    companion object {

        /**
         * Removes all attribute modifiers from the given item meta, if the modifiers
         * were added by an [AttributeModifiers] instance (added by an attachment).
         */
        @JvmStatic
        fun stripAllAttributeModifiers(meta: ItemMeta) {
            if (!meta.hasAttributeModifiers())
                return

            for ((attribute, modifier) in meta.attributeModifiers!!.entries()) {
                if (!modifier.key.namespace.equals(WeaponMechanicsPlus.getInstance().name, ignoreCase = true))
                    continue

                meta.removeAttributeModifier(attribute, modifier)
            }
        }
    }
}
