package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.damage.DamageDropoff
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class DamageModifier : Serializer<DamageModifier> {

    var baseDamage: DoubleModifier? = null
    var explosionDamage: DoubleModifier? = null
    var fireTicks: IntegerModifier? = null
    var armorDamage: IntegerModifier? = null
    var dropoffOverride: DamageDropoff? = null

    // An attachment can either add, or replace the DamageModifier
    var replaceDefaultDamageModifier = false
    var damageModifier: me.deecaad.weaponmechanics.weapon.damage.DamageModifier? = null

    // An attachment can either add, or replace Mechanics
    var damageMechanicsModifier: MechanicsModifier? = null
    var killMechanicsModifier: MechanicsModifier? = null
    var backstabMechanicsModifier: MechanicsModifier? = null
    var criticalHitMechanicsModifier: MechanicsModifier? = null
    var headMechanicsModifier: MechanicsModifier? = null
    var bodyMechanicsModifier: MechanicsModifier? = null
    var armsMechanicsModifier: MechanicsModifier? = null
    var legsMechanicsModifier: MechanicsModifier? = null
    var feetMechanicsModifier: MechanicsModifier? = null


    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        baseDamage: DoubleModifier?,
        explosionDamage: DoubleModifier?,
        fireTicks: IntegerModifier?,
        armorDamage: IntegerModifier?,
        dropoffOverride: DamageDropoff?,
        replaceDefaultDamageModifier: Boolean,
        damageModifier: me.deecaad.weaponmechanics.weapon.damage.DamageModifier?,
        damageMechanicsModifier: MechanicsModifier?,
        killMechanicsModifier: MechanicsModifier?,
        backstabMechanicsModifier: MechanicsModifier?,
        criticalHitMechanicsModifier: MechanicsModifier?,
        headMechanicsModifier: MechanicsModifier?,
        bodyMechanicsModifier: MechanicsModifier?,
        armsMechanicsModifier: MechanicsModifier?,
        legsMechanicsModifier: MechanicsModifier?,
        feetMechanicsModifier: MechanicsModifier?
    ) {
        this.baseDamage = baseDamage
        this.explosionDamage = explosionDamage
        this.fireTicks = fireTicks
        this.armorDamage = armorDamage
        this.dropoffOverride = dropoffOverride
        this.replaceDefaultDamageModifier = replaceDefaultDamageModifier
        this.damageModifier = damageModifier
        this.damageMechanicsModifier = damageMechanicsModifier
        this.killMechanicsModifier = killMechanicsModifier
        this.backstabMechanicsModifier = backstabMechanicsModifier
        this.criticalHitMechanicsModifier = criticalHitMechanicsModifier
        this.headMechanicsModifier = headMechanicsModifier
        this.bodyMechanicsModifier = bodyMechanicsModifier
        this.armsMechanicsModifier = armsMechanicsModifier
        this.legsMechanicsModifier = legsMechanicsModifier
        this.feetMechanicsModifier = feetMechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): DamageModifier {
        val damage = data.of("Base_Damage").serialize(DoubleModifier::class.java)
        val explosionDamage = data.of("Base_Explosion_Damage").serialize(DoubleModifier::class.java)
        val fireTicks = data.of("Fire_Ticks").serialize(IntegerModifier::class.java)
        val armorDamage = data.of("Armor_Damage").serialize(IntegerModifier::class.java)
        val dropoffOverride = data.of("Damage_Dropoff").serialize(DamageDropoff::class.java)

        val replaceDamageModifier = data.of("Replace_Damage_Modifier").serialize(me.deecaad.weaponmechanics.weapon.damage.DamageModifier::class.java)
        val addDamageModifier = data.of("Add_Damage_Modifier").serialize(me.deecaad.weaponmechanics.weapon.damage.DamageModifier::class.java)
        val isReplace = replaceDamageModifier != null
        val damageModifier = if (isReplace) replaceDamageModifier else addDamageModifier

        if (replaceDamageModifier != null && addDamageModifier != null) {
            throw data.exception(null,
                "You cannot use both 'Replace_Damage_Modifier' and 'Add_Damage_Modifier' on the same attachment at the same time",
                "If you want to replace the weapon's damage modifiers, use 'Replace_Damage_Modifier'",
                "If you want to add new modifiers on top of the existing modifiers, use 'Add_Damage_Modifier'")
        }

        val damageMechanicsModifier = data.serializeMechanicsModifier("Mechanics")
        val killMechanicsModifier = data.serializeMechanicsModifier("Kill_Mechanics")
        val backstabMechanicsModifier = data.serializeMechanicsModifier("Backstab.Mechanics")
        val criticalHitMechanicsModifier = data.serializeMechanicsModifier("Critical_Hit.Mechanics")
        val headMechanicsModifier = data.serializeMechanicsModifier("Head.Mechanics")
        val bodyMechanicsModifier = data.serializeMechanicsModifier("Body.Mechanics")
        val armsMechanicsModifier = data.serializeMechanicsModifier("Arms.Mechanics")
        val legsMechanicsModifier = data.serializeMechanicsModifier("Legs.Mechanics")
        val feetMechanicsModifier = data.serializeMechanicsModifier("Feet.Mechanics")

        return DamageModifier(damage, explosionDamage, fireTicks, armorDamage, dropoffOverride, isReplace, damageModifier,
            damageMechanicsModifier, killMechanicsModifier, backstabMechanicsModifier, criticalHitMechanicsModifier,
            headMechanicsModifier, bodyMechanicsModifier, armsMechanicsModifier, legsMechanicsModifier, feetMechanicsModifier)
    }
}