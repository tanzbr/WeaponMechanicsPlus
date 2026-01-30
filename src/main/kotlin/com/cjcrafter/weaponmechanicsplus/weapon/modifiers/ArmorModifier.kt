/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.AttributeModifiers
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier
import kotlin.jvm.optionals.getOrNull

class ArmorModifier : Serializer<ArmorModifier> {

    var attributeModifiers: AttributeModifiers? = null

    var bulletResistance: DoubleModifier? = null
    var explosionResistance: DoubleModifier? = null
    var equipMechanicsModifier: MechanicsModifier? = null
    var dequipMechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        attributeModifiers: AttributeModifiers?,
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
        val attributeModifiers = data.of("Attributes").serialize(AttributeModifiers::class.java).getOrNull()

        val bulletResistance = data.of("Bullet_Resistance").serialize(DoubleModifier::class.java).getOrNull()
        val explosionResistance = data.of("Explosion_Resistance").serialize(DoubleModifier::class.java).getOrNull()
        val equipMechanicsModifier = data.serializeMechanicsModifier("Equip_Mechanics")
        val dequipMechanicsModifier = data.serializeMechanicsModifier("Dequip_Mechanics")

        return ArmorModifier(attributeModifiers, bulletResistance, explosionResistance, equipMechanicsModifier, dequipMechanicsModifier)
    }
}