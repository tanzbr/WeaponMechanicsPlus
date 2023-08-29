package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class ShootModifier : Serializer<ShootModifier> {

    var projectileAmount: IntegerModifier? = null
    var projectileSpeed: DoubleModifier? = null
    var mechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(projectileAmount: IntegerModifier?, projectileSpeed: DoubleModifier?, mechanicsModifier: MechanicsModifier?) {
        this.projectileAmount = projectileAmount
        this.projectileSpeed = projectileSpeed
        this.mechanicsModifier = mechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ShootModifier {

        val projectileAmount = data.of("Projectile_Amount").serialize(IntegerModifier::class.java)
        val projectileSpeed = data.of("Projectile_Speed").serialize(DoubleModifier::class.java)
        val mechanicsModifier = data.serializeMechanicsModifier()

        return ShootModifier(projectileAmount, projectileSpeed, mechanicsModifier)
    }
}