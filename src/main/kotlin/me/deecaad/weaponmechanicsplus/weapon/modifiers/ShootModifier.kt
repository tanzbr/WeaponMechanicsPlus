package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier

class ShootModifier : Serializer<ShootModifier> {

    var projectileAmount: IntegerModifier? = null
    var projectileSpeed: DoubleModifier? = null

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(projectileAmount: IntegerModifier?, projectileSpeed: DoubleModifier?) {
        this.projectileAmount = projectileAmount
        this.projectileSpeed = projectileSpeed
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ShootModifier {

        val projectileAmount = data.of("Projectile_Amount").serialize(IntegerModifier::class.java)
        val projectileSpeed = data.of("Projectile_Speed").serialize(DoubleModifier::class.java)

        return ShootModifier(projectileAmount, projectileSpeed)
    }
}