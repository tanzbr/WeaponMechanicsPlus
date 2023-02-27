package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile

class ShootModifier : Serializer<ShootModifier> {
    var overrideProjectile: Projectile? = null
        private set

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(overrideProjectile: Projectile?) {
        this.overrideProjectile = overrideProjectile
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ShootModifier {
        val overrideProjectile = data.of("Override_Projectile").serialize(
            Projectile::class.java
        )
        return ShootModifier(overrideProjectile)
    }
}