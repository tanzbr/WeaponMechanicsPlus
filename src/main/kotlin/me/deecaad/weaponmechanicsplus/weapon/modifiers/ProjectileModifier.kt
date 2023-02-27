package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile

class ProjectileModifier : Serializer<ProjectileModifier> {

    private var replaceProjectile: Projectile? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(replaceProjectile: Projectile?) {
        this.replaceProjectile = replaceProjectile
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ProjectileModifier {
        val replaceProjectile = data.of("Set_Projectile").serialize(Projectile::class.java)
        return ProjectileModifier(replaceProjectile)
    }
}