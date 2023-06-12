package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.damage.DamageDropoff
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier

class DamageModifier : Serializer<DamageModifier> {

    var baseDamage: DoubleModifier? = null
    var explosionDamage: DoubleModifier? = null
    var fireTicks: IntegerModifier? = null
    var armorDamage: IntegerModifier? = null
    var dropoffOverride: DamageDropoff? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(damage: DoubleModifier?, explosionDamage: DoubleModifier?, fireTicks: IntegerModifier?, armorDamage: IntegerModifier?, dropoffOverride: DamageDropoff?) {
        this.baseDamage = damage
        this.explosionDamage = explosionDamage
        this.fireTicks = fireTicks
        this.armorDamage = armorDamage
        this.dropoffOverride = dropoffOverride
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): DamageModifier {
        val damage = data.of("Base_Damage").serialize(DoubleModifier::class.java)
        val explosionDamage = data.of("Base_Explosion_Damage").serialize(DoubleModifier::class.java)
        val fireTicks = data.of("Fire_Ticks").serialize(IntegerModifier::class.java)
        val armorDamage = data.of("Armor_Damage").serialize(IntegerModifier::class.java)
        val dropoffOverride = data.of("Damage_Dropoff").serialize(DamageDropoff::class.java)

        return DamageModifier(damage, explosionDamage, fireTicks, armorDamage, dropoffOverride)
    }
}