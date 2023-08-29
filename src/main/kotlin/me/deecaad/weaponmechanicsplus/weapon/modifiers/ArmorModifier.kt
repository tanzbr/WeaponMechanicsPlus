package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.AttributeModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.AttributeModifier.Companion.getAttributeModifiers
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class ArmorModifier : Serializer<ArmorModifier> {

    lateinit var attributeModifiers: List<AttributeModifier>

    var bulletResistance = 0.0
    var explosionResistance = 0.0
    var equipMechanicsModifier: MechanicsModifier? = null
    var dequipMechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        attributeModifiers: List<AttributeModifier>,
        bulletResistance: Double,
        explosionResistance: Double,
        equipMechanicsModifier: MechanicsModifier?,
        dequipMechanicsModifier: MechanicsModifier?
    ) {
        this.attributeModifiers = attributeModifiers
        this.bulletResistance = bulletResistance
        this.explosionResistance = explosionResistance
        this.equipMechanicsModifier = equipMechanicsModifier
        this.dequipMechanicsModifier = dequipMechanicsModifier
    }

    override fun serialize(data: SerializeData): ArmorModifier {
        val attributeModifiers = data.ofList("Attributes").getAttributeModifiers()

        val bulletResistance = data.of("Bullet_Resistance").getDouble(0.0)
        val explosionResistance = data.of("Explosion_Resistance").getDouble(0.0)
        val equipMechanicsModifier = data.serializeMechanicsModifier("Equip_Mechanics")
        val dequipMechanicsModifier = data.serializeMechanicsModifier("Dequip_Mechanics")

        return ArmorModifier(attributeModifiers, bulletResistance, explosionResistance, equipMechanicsModifier, dequipMechanicsModifier)
    }
}