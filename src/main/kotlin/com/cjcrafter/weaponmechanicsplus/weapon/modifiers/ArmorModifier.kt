package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.AttributeModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.AttributeModifier.Companion.getAttributeModifiers
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class ArmorModifier : Serializer<ArmorModifier> {

    lateinit var attributeModifiers: List<AttributeModifier>

    var bulletResistance: DoubleModifier? = null
    var explosionResistance: DoubleModifier? = null
    var equipMechanicsModifier: MechanicsModifier? = null
    var dequipMechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        attributeModifiers: List<AttributeModifier>,
        bulletResistance: DoubleModifier?,
        explosionResistance: DoubleModifier?,
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

        val bulletResistance = data.of("Bullet_Resistance").serialize(DoubleModifier::class.java)
        val explosionResistance = data.of("Explosion_Resistance").serialize(DoubleModifier::class.java)
        val equipMechanicsModifier = data.serializeMechanicsModifier("Equip_Mechanics")
        val dequipMechanicsModifier = data.serializeMechanicsModifier("Dequip_Mechanics")

        return ArmorModifier(attributeModifiers, bulletResistance, explosionResistance, equipMechanicsModifier, dequipMechanicsModifier)
    }
}