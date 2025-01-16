package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier
import me.deecaad.weaponmechanics.weapon.shoot.spread.Spread
import kotlin.jvm.optionals.getOrNull

class ShootModifier : Serializer<ShootModifier> {

    var projectileAmount: IntegerModifier? = null
    var projectileSpeed: DoubleModifier? = null
    var fullyAutomaticShotsPerSecond: IntegerModifier? = null
    var mechanicsModifier: MechanicsModifier? = null
    var baseSpread: DoubleModifier? = null
    var overrideSpread: Spread? = null
    var skipAttractMobs: Boolean = false
    var recoilModifier: RecoilModifier? = null

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(
        projectileAmount: IntegerModifier?,
        projectileSpeed: DoubleModifier?,
        fullyAutomaticShotsPerSecond: IntegerModifier?,
        mechanicsModifier: MechanicsModifier?,
        baseSpread: DoubleModifier?,
        overrideSpread: Spread?,
        skipAttractMobs: Boolean,
        recoilModifier: RecoilModifier?,
    ) {
        this.projectileAmount = projectileAmount
        this.projectileSpeed = projectileSpeed
        this.fullyAutomaticShotsPerSecond = fullyAutomaticShotsPerSecond
        this.mechanicsModifier = mechanicsModifier
        this.baseSpread = baseSpread
        this.overrideSpread = overrideSpread
        this.skipAttractMobs = skipAttractMobs
        this.recoilModifier = recoilModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ShootModifier {

        val projectileAmount = data.of("Projectile_Amount").serialize(IntegerModifier::class.java).getOrNull()
        val projectileSpeed = data.of("Projectile_Speed").serialize(DoubleModifier::class.java).getOrNull()
        val fullyAutomaticShotsPerSecond = data.of("Fully_Automatic_Shots_Per_Second").serialize(IntegerModifier::class.java).getOrNull()
        val mechanicsModifier = data.serializeMechanicsModifier()

        val baseSpread = data.of("Base_Spread").serialize(DoubleModifier::class.java).getOrNull()
        val overrideSpread = data.of("Override_Spread").serialize(Spread::class.java).getOrNull()

        val skipAttractMobs = data.of("Skip_Attract_Mobs").getBool().orElse(false)

        val recoilModifier = data.of("Recoil_Modifier").serialize(RecoilModifier::class.java).getOrNull()

        return ShootModifier(
            projectileAmount,
            projectileSpeed,
            fullyAutomaticShotsPerSecond,
            mechanicsModifier,
            baseSpread,
            overrideSpread,
            skipAttractMobs,
            recoilModifier,
        )
    }
}