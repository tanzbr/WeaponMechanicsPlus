package me.deecaad.weaponmechanicsplus.weapon.guidedprojectile

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException

class GuidedProjectile : Serializer<GuidedProjectile> {

    /**
     * Default constructor for serializer
     */
    constructor() {}

    override fun getKeyword(): String {
        return "Guided_Projectile"
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): GuidedProjectile {

        return GuidedProjectile();
    }
}